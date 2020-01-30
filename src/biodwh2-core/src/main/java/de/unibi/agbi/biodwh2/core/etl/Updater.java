package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Updater {
    private static final Logger logger = LoggerFactory.getLogger(Updater.class);

    public abstract Version getNewestVersion() throws UpdaterException;

    public final boolean update(Workspace workspace, DataSource dataSource) throws UpdaterException {
        Version newestVersion = getNewestVersion();
        Version workspaceVersion = dataSource.getMetadata().version;
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            logger.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return true;
        }
        logger.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                    (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        if (tryUpdateFiles(workspace, dataSource)) {
            updateDataSourceMetadata(workspace, dataSource, newestVersion);
            return true;
        }
        return false;
    }

    public final boolean updateManually(Workspace workspace, DataSource dataSource, String version) {
        Version workspaceVersion = dataSource.getMetadata().version;
        Version newestVersion = Version.tryParse(version);
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            logger.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return true;
        }
        logger.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                    (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        updateDataSourceMetadata(workspace, dataSource, newestVersion);
        return true;
    }

    private boolean isDataSourceUpToDate(Version newestVersion, Version workspaceVersion) {
        return workspaceVersion != null && newestVersion.compareTo(workspaceVersion) == 0;
    }

    protected abstract boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException;

    final void updateDataSourceMetadata(Workspace workspace, DataSource dataSource, Version version) {
        dataSource.getMetadata().version = version;
        dataSource.getMetadata().setUpdateDateTimeNow();
        dataSource.getMetadata().sourceFileNames = dataSource.listSourceFiles(workspace);
    }

    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
