package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Updater {
    public abstract Version getNewestVersion();

    public final boolean update(Workspace workspace, DataSource dataSource) throws UpdaterException {
        Version newestVersion = getNewestVersion();
        Version workspaceVersion = dataSource.getMetadata().version;
        if (isDataSourceUpToDate(newestVersion, workspaceVersion))
            return false;
        if (tryUpdateFiles(workspace, dataSource)) {
            dataSource.getMetadata().version = newestVersion;
            dataSource.getMetadata().setUpdateDateTimeNow();
            dataSource.getMetadata().sourceFileNames = dataSource.listSourceFiles(workspace);
            try {
                dataSource.saveMetadata(workspace);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
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
