package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.DataSourceMetadata;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Updater(final D dataSource) {
        this.dataSource = dataSource;
    }

    protected final D dataSource;

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

    public final UpdateState update(Workspace workspace) throws UpdaterException {
        final Version newestVersion = getNewestVersion();
        final Version workspaceVersion = dataSource.getMetadata().version;
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                        (workspaceVersion == null ? "none" : workspaceVersion) + ", new: " + newestVersion + ")");
        if (tryUpdateFiles(workspace)) {
            updateDataSourceMetadata(workspace, newestVersion);
            return UpdateState.UPDATED;
        }
        return UpdateState.FAILED;
    }

    public final UpdateState updateManually(Workspace workspace, String version) {
        final Version workspaceVersion = dataSource.getMetadata().version;
        final Version newestVersion = Version.tryParse(version);
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                        (workspaceVersion == null ? "none" : workspaceVersion) + ", new: " + newestVersion + ")");
        updateDataSourceMetadata(workspace, newestVersion);
        return UpdateState.UPDATED;
    }

    private boolean isDataSourceUpToDate(Version newestVersion, Version workspaceVersion) {
        return workspaceVersion != null && newestVersion.compareTo(workspaceVersion) == 0;
    }

    protected abstract boolean tryUpdateFiles(Workspace workspace) throws UpdaterException;

    private void updateDataSourceMetadata(Workspace workspace, Version version) {
        final DataSourceMetadata metadata = dataSource.getMetadata();
        metadata.version = version;
        metadata.setUpdateDateTimeNow();
        metadata.sourceFileNames = new ArrayList<>();
        Collections.addAll(metadata.sourceFileNames, dataSource.listSourceFiles(workspace));
    }

    public final boolean isDataSourceUpToDate() {
        Version newestVersion = tryGetNewestVersion();
        final Version workspaceVersion = dataSource.getMetadata().version;
        return isDataSourceUpToDate(newestVersion, workspaceVersion);
    }


    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
