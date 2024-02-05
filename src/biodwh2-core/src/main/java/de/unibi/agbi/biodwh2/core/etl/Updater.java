package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.cache.DataSourceVersion;
import de.unibi.agbi.biodwh2.core.cache.OnlineVersionCache;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Updater<D extends DataSource> {
    public enum UpdateState {
        FAILED,
        ALREADY_UP_TO_DATE,
        UPDATED
    }

    private static final Logger LOGGER = LogManager.getLogger(Updater.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final D dataSource;

    public Updater(final D dataSource) {
        this.dataSource = dataSource;
    }

    public final Version tryGetNewestVersion(final Workspace workspace) {
        try {
            return getNewestVersion(workspace);
        } catch (UpdaterException e) {
            final DataSourceVersion latest = OnlineVersionCache.getInstance().getLatest(dataSource.getId());
            if (latest == null || latest.getVersion() == null) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Failed to get newest version for data source '" + dataSource.getId() +
                                "' directly or from online version cache.", e);
            } else {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Failed to get newest version for data source '" + dataSource.getId() +
                                "' directly. Using online version cache instead.", e.getMessage());
                return latest.getVersion();
            }
        }
        return null;
    }

    protected abstract Version getNewestVersion(final Workspace workspace) throws UpdaterException;

    public final UpdateState update(final Workspace workspace) throws UpdaterException {
        final Version newestVersion = tryGetNewestVersion(workspace);
        final Version workspaceVersion = dataSource.getMetadata().version;
        final boolean expectedFilesPresent = areExpectedFilesPresent(workspace);
        final boolean isUpToDate = isDataSourceUpToDate(newestVersion, workspaceVersion);
        if (isUpToDate && expectedFilesPresent) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled()) {
            if (isUpToDate) {
                final String versionInfo =
                        versionNotAvailable() ? "" : (" (updating to version " + newestVersion + ")");
                LOGGER.info("Some files of data source '" + dataSource.getId() + "' are missing" + versionInfo);
            } else {
                final String versionInfo = versionNotAvailable() ? "" :
                                           (" (old: " + (workspaceVersion == null ? "none" : workspaceVersion) +
                                            ", new: " + newestVersion + ")");
                LOGGER.info("New version of data source '" + dataSource.getId() + "' found" + versionInfo);
            }
        }
        if (tryUpdateFiles(workspace)) {
            updateDataSourceMetadata(workspace, newestVersion);
            return UpdateState.UPDATED;
        }
        return UpdateState.FAILED;
    }

    private boolean areExpectedFilesPresent(final Workspace workspace) {
        for (final String fileName : expectedFileNames())
            if (!Paths.get(dataSource.resolveSourceFilePath(workspace, fileName)).toFile().exists())
                return false;
        return true;
    }

    protected String[] expectedFileNames() {
        return new String[0];
    }

    private boolean isDataSourceUpToDate(final Version newestVersion, final Version workspaceVersion) {
        if (versionNotAvailable())
            return false;
        return workspaceVersion != null && newestVersion.compareTo(workspaceVersion) == 0;
    }

    protected boolean versionNotAvailable() {
        return false;
    }

    protected abstract boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException;

    private void updateDataSourceMetadata(final Workspace workspace, final Version version) {
        final DataSourceMetadata metadata = dataSource.getMetadata();
        metadata.version = version;
        metadata.setUpdateDateTimeNow();
        metadata.sourceFileNames = new ArrayList<>();
        Collections.addAll(metadata.sourceFileNames, dataSource.listSourceFiles(workspace));
    }

    public final boolean isDataSourceUpToDate(final Workspace workspace) {
        final Version newestVersion = tryGetNewestVersion(workspace);
        final Version workspaceVersion = dataSource.getMetadata().version;
        return isDataSourceUpToDate(newestVersion, workspaceVersion);
    }

    protected static Version convertDateTimeToVersion(final LocalDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }

    protected String getWebsiteSource(final String url) throws UpdaterConnectionException {
        try {
            return HTTPClient.getWebsiteSource(url);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version", e);
        }
    }

    protected String getWebsiteSource(final String url, int retries) throws UpdaterConnectionException {
        try {
            return HTTPClient.getWebsiteSource(url, retries);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version", e);
        }
    }

    protected void downloadFileAsBrowser(final Workspace workspace, final String url,
                                         final String fileName) throws UpdaterException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        final int[] rotateIndex = {0};
        final long[] lastTime = {System.currentTimeMillis()};
        char[] rotateChars = {'|', '/', '-', '\\'};
        System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        System.out.print(
                " [INFO ] " + getClass().getName() + " - Downloading file '" + fileName + "' " + rotateChars[0] +
                " [  0%]");
        try {
            HTTPClient.downloadFileAsBrowser(url, filePath, (position, length) -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime[0] > 1000) {
                    rotateIndex[0] = (rotateIndex[0] + 1) % rotateChars.length;
                    lastTime[0] += 1000;
                }
                System.out.print("\b\b\b\b\b\b\b\b");
                System.out.print(rotateChars[rotateIndex[0]]);
                if (length == null) {
                    System.out.print(" [  ?%]");
                } else {
                    String percentage = String.format("%1$3s", (int) (position * 100.0 / length));
                    System.out.print(" [" + percentage + "%]");
                }
            });
        } catch (IOException e) {
            System.out.print("\b\b\b\b\b\b\b\bX [  ?%]");
            throw new UpdaterConnectionException("Failed to download file '" + url + "'", e);
        }
        System.out.println("\b\b\b\b\b\b\b\b\u2713 [100%]");
    }
}
