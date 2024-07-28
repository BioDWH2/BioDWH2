package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.cache.DataSourceVersion;
import de.unibi.agbi.biodwh2.core.cache.OnlineVersionCache;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.SpeciesFilter;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import org.apache.commons.io.file.PathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;

public abstract class Updater<D extends DataSource> {
    public enum UpdateState {
        FAILED,
        ALREADY_UP_TO_DATE,
        UPDATED
    }

    private static final Logger LOGGER = LogManager.getLogger(Updater.class);
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected static final char[] ROTATE_CHARS = new char[]{'|', '/', '-', '\\'};

    protected final D dataSource;
    protected SpeciesFilter speciesFilter;

    public Updater(final D dataSource) {
        this.dataSource = dataSource;
        speciesFilter = new SpeciesFilter();
    }

    public final Version tryGetNewestVersion(final Workspace workspace) {
        speciesFilter = SpeciesFilter.fromWorkspaceDataSource(workspace, dataSource);
        try {
            return getNewestVersion(workspace);
        } catch (UpdaterException e) {
            final DataSourceVersion latest = OnlineVersionCache.getInstance().getLatest(dataSource.getId());
            if (latest == null || latest.getVersion() == null) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn(
                            "Failed to get newest version for data source '{}' directly or from online version cache.",
                            dataSource.getId(), e);
            } else {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn(
                            "Failed to get newest version for data source '{}' directly. Using online version cache instead.",
                            dataSource.getId(), e.getMessage());
                return latest.getVersion();
            }
        }
        return null;
    }

    protected abstract Version getNewestVersion(final Workspace workspace) throws UpdaterException;

    public final UpdateState update(final Workspace workspace) throws UpdaterException {
        final Version newestVersion = tryGetNewestVersion(workspace);
        final var metadata = dataSource.getMetadata();
        final Version workspaceVersion = metadata.version;
        final boolean expectedFilesPresent = areExpectedFilesPresent(workspace);
        final var localUpdateDateTime = metadata.getLocalUpdateDateTime();
        final boolean isUpToDate = isDataSourceUpToDate(newestVersion, workspaceVersion, localUpdateDateTime);
        if (isUpToDate && expectedFilesPresent) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '{}' is already up-to-date ({})", dataSource.getId(), newestVersion);
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled()) {
            if (isUpToDate) {
                final String versionInfo =
                        versionNotAvailable() ? "" : (" (updating to version " + newestVersion + ")");
                LOGGER.info("Some files of data source '{}' are missing{}", dataSource.getId(), versionInfo);
            } else {
                final String versionInfo = versionNotAvailable() ? "" :
                                           (" (old: " + (workspaceVersion == null ? "none" : workspaceVersion) +
                                            ", new: " + newestVersion + ")");
                LOGGER.info("New version of data source '{}' found{}", dataSource.getId(), versionInfo);
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
            if (!dataSource.resolveSourceFilePath(workspace, fileName).toFile().exists())
                return false;
        return true;
    }

    protected String[] expectedFileNames() {
        return new String[0];
    }

    private boolean isDataSourceUpToDate(final Version newestVersion, final Version workspaceVersion,
                                         LocalDateTime localUpdateDateTime) {
        if (versionNotAvailable()) {
            if (localUpdateDateTime == null)
                return false;
            // If we have no version but already updated, only update again after 24 hours have passed
            return Duration.between(LocalDateTime.now(), localUpdateDateTime).toDays() < 1;
        }
        return workspaceVersion != null && (newestVersion == null || newestVersion.compareTo(workspaceVersion) == 0);
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
        final var metadata = dataSource.getMetadata();
        final Version workspaceVersion = metadata.version;
        final var localUpdateDateTime = metadata.getLocalUpdateDateTime();
        return isDataSourceUpToDate(newestVersion, workspaceVersion, localUpdateDateTime);
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
        final var filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        downloadFileAsBrowser(url, fileName, filePath,
                              (progressReporter) -> HTTPClient.downloadFileAsBrowser(url, filePath, progressReporter));
    }

    protected void downloadFileAsBrowser(final Workspace workspace, final String url, final String fileName,
                                         final String username, final String password) throws UpdaterException {
        final var filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        downloadFileAsBrowser(url, fileName, filePath,
                              (progressReporter) -> HTTPClient.downloadFileAsBrowser(url, filePath, username, password,
                                                                                     progressReporter));
    }

    protected void downloadFileAsBrowser(final String url, final String fileName, final Path filePath,
                                         final FileUtils.IOConsumer<BiConsumer<Long, Long>> ioConsumer) throws UpdaterException {
        downloadFileAsBrowser(url, fileName, filePath, ioConsumer, 5);
    }

    protected void downloadFileAsBrowser(final String url, final String fileName, final Path filePath,
                                         final FileUtils.IOConsumer<BiConsumer<Long, Long>> ioConsumer,
                                         final int retries) throws UpdaterException {
        final int[] rotateIndex = {0};
        final long[] lastTime = {System.currentTimeMillis()};
        var requestedFileSize = new Long[]{null};
        var success = false;
        var counter = 0;
        while (counter < retries && !success) {
            success = true;
            counter++;
            System.out.print(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            System.out.print(
                    " [INFO ] " + getClass().getName() + " - Downloading file '" + fileName + "' " + ROTATE_CHARS[0] +
                    " [  0%]");
            try {
                ioConsumer.accept((position, length) -> {
                    if (length != null && length > 0)
                        requestedFileSize[0] = length;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime[0] > 500) {
                        rotateIndex[0] = (rotateIndex[0] + 1) % ROTATE_CHARS.length;
                        lastTime[0] += 1000;
                    }
                    System.out.print("\b\b\b\b\b\b\b\b");
                    System.out.print(ROTATE_CHARS[rotateIndex[0]]);
                    if (length == null || length <= 0) {
                        System.out.print(" [  ?%]");
                    } else {
                        final String percentage = String.format("%1$3s", (int) (position * 100.0 / length));
                        System.out.print(" [" + percentage + "%]");
                    }
                });
            } catch (IOException e) {
                System.out.println("\b\b\b\b\b\b\b\bX [  ?%]");
                success = false;
                if (counter == retries) {
                    throw new UpdaterConnectionException("Failed to download file '" + url + "'", e);
                } else {
                    LOGGER.warn("Failed to download file '{}' (try {}/{}), retrying in 5 seconds...", url, counter,
                                retries);
                    try {
                        // Small wait to not overpower the server
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }
            }
            if (requestedFileSize[0] != null) {
                try {
                    final var storedFileSize = PathUtils.sizeOf(filePath);
                    if (storedFileSize != requestedFileSize[0]) {
                        System.out.println("\b\b\b\b\b\b\b\bX [  ?%]");
                        success = false;
                        if (counter == retries) {
                            throw new UpdaterConnectionException(
                                    "Failed to download file '" + url + "' (size mismatch)");
                        } else {
                            LOGGER.warn("Failed to download file '{}' (size mismatch), try {}/{}", url, counter,
                                        retries);
                            continue;
                        }
                    }
                } catch (IOException ignored) {
                }
            }
            System.out.println("\b\b\b\b\b\b\b\b\u2713 [100%]");
        }
    }
}
