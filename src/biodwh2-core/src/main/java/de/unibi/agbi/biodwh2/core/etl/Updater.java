package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.cache.DataSourceVersion;
import de.unibi.agbi.biodwh2.core.cache.OnlineVersionCache;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
