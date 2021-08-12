package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(Updater.class);

    protected final D dataSource;

    public Updater(final D dataSource) {
        this.dataSource = dataSource;
    }

    public final Version tryGetNewestVersion() {
        try {
            return getNewestVersion();
        } catch (UpdaterException e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Failed to get newest version for data source '" + dataSource.getId() + "'", e);
        }
        return null;
    }

    public abstract Version getNewestVersion() throws UpdaterException;

    public final UpdateState update(final Workspace workspace) throws UpdaterException {
        final Version newestVersion = getNewestVersion();
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

    public final UpdateState updateManually(final Workspace workspace, final String version) {
        final Version workspaceVersion = dataSource.getMetadata().version;
        final Version newestVersion = Version.tryParse(version);
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled()) {
            final String versionInfo = versionNotAvailable() ? "" :
                                       (" (old: " + (workspaceVersion == null ? "none" : workspaceVersion) + ", new: " +
                                        newestVersion + ")");
            LOGGER.info("New version of data source '" + dataSource.getId() + "' found" + versionInfo);
        }
        updateDataSourceMetadata(workspace, newestVersion);
        return UpdateState.UPDATED;
    }

    public final boolean isDataSourceUpToDate() {
        final Version newestVersion = tryGetNewestVersion();
        final Version workspaceVersion = dataSource.getMetadata().version;
        return isDataSourceUpToDate(newestVersion, workspaceVersion);
    }

    protected static Version convertDateTimeToVersion(final LocalDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
