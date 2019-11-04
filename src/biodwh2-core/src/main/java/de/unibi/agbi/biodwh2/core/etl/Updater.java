package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
            return false;
        }
        logger.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                    (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        if (tryUpdateFiles(workspace, dataSource)) {
            dataSource.getMetadata().version = newestVersion;
            dataSource.getMetadata().setUpdateDateTimeNow();
            dataSource.getMetadata().sourceFileNames = dataSource.listSourceFiles(workspace);
            try {
                dataSource.saveMetadata(workspace);
            } catch (IOException e) {
                logger.error("Failed to save metadata for data source '" + dataSource.getId() + "'", e);
                return false;
            }
            return true;
        }
        return false;
    }

    public final boolean integrate(Workspace workspace, DataSource dataSource, String version) throws UpdaterException {
        Version workspaceVersion = dataSource.getMetadata().version;
        Version newestVersion = Version.tryParse(version);
        if (isDataSourceUpToDate(newestVersion, workspaceVersion)) {
            logger.info("Data source '" + dataSource.getId() + "' is already up-to-date (" + newestVersion + ")");
            return false;
        }
        logger.info("New version of data source '" + dataSource.getId() + "' found (old: " +
                    (workspaceVersion != null ? workspaceVersion : "none") + ", new: " + newestVersion + ")");
        dataSource.getMetadata().version = newestVersion;
        dataSource.getMetadata().setUpdateDateTimeNow();
        dataSource.getMetadata().sourceFileNames = dataSource.listSourceFiles(workspace);
        try {
            dataSource.saveMetadata(workspace);
        } catch (IOException e) {
            logger.error("Failed to save metadata for data source '" + dataSource.getId() + "'", e);
            return false;
        }
        return true;
    }

    private boolean isDataSourceUpToDate(Version newestVersion, Version workspaceVersion) {
        return workspaceVersion != null && newestVersion.compareTo(workspaceVersion) == 0;
    }

    protected abstract boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException;

    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
