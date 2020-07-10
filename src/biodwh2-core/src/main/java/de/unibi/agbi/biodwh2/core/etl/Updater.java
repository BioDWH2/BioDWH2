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
        Failed,
        AlreadyUpToDate,
        Updated
    }

    private static final Logger logger = LoggerFactory.getLogger(Updater.class);

    public abstract Version getNewestVersion() throws UpdaterException;

    public final UpdateState update(Workspace workspace, D dataSource) throws UpdaterException {
        Version newestVersion = getNewestVersion();
        Version workspaceVersion = dataSource.getMetadata().version;
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            logger.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.AlreadyUpToDate;
        }
        logger.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                    (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        if (tryUpdateFiles(workspace, dataSource)) {
            updateDataSourceMetadata(workspace, dataSource, newestVersion);
            return UpdateState.Updated;
        }
        return UpdateState.Failed;
    }

    public final UpdateState updateManually(Workspace workspace, D dataSource, String version) {
        Version workspaceVersion = dataSource.getMetadata().version;
        Version newestVersion = Version.tryParse(version);
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            logger.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return UpdateState.AlreadyUpToDate;
        }
        logger.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                    (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        updateDataSourceMetadata(workspace, dataSource, newestVersion);
        return UpdateState.Updated;
    }

    private boolean isDataSourceUpToDate(Version newestVersion, Version workspaceVersion) {
        return workspaceVersion != null && newestVersion.compareTo(workspaceVersion) == 0;
    }

    protected abstract boolean tryUpdateFiles(Workspace workspace, D dataSource) throws UpdaterException;

    private void updateDataSourceMetadata(Workspace workspace, D dataSource, Version version) {
        DataSourceMetadata metadata = dataSource.getMetadata();
        metadata.version = version;
        metadata.setUpdateDateTimeNow();
        metadata.sourceFileNames = new ArrayList<>();
        Collections.addAll(metadata.sourceFileNames, dataSource.listSourceFiles(workspace));
    }

    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
