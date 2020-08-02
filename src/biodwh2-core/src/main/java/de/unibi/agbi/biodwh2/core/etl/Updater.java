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

    public abstract Version getNewestVersion() throws UpdaterException;

    public final UpdateState update(Workspace workspace, D dataSource) throws UpdaterException {
        final Version newestVersion = getNewestVersion();
        final Version workspaceVersion = dataSource.getMetadata().version;
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                        (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        if (tryUpdateFiles(workspace, dataSource)) {
            updateDataSourceMetadata(workspace, dataSource, newestVersion);
            return UpdateState.UPDATED;
        }
        return UpdateState.FAILED;
    }

    public final UpdateState updateManually(Workspace workspace, D dataSource, String version) {
        final Version workspaceVersion = dataSource.getMetadata().version;
        final Version newestVersion = Version.tryParse(version);
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.ALREADY_UP_TO_DATE;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                        (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        updateDataSourceMetadata(workspace, dataSource, newestVersion);
        return UpdateState.UPDATED;
    }

    private boolean isDataSourceUpToDate(Version newestVersion, Version workspaceVersion) {
        return workspaceVersion != null && newestVersion.compareTo(workspaceVersion) == 0;
    }

    protected abstract boolean tryUpdateFiles(Workspace workspace, D dataSource) throws UpdaterException;

    private void updateDataSourceMetadata(Workspace workspace, D dataSource, Version version) {
        final DataSourceMetadata metadata = dataSource.getMetadata();
        metadata.version = version;
        metadata.setUpdateDateTimeNow();
        metadata.sourceFileNames = new ArrayList<>();
        Collections.addAll(metadata.sourceFileNames, dataSource.listSourceFiles(workspace));
    }

    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
