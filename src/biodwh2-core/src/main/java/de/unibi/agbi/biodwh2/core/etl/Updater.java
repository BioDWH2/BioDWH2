package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.model.Version;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Updater {
    private final Workspace workspace;

    public Updater(Workspace workspace) {
        this.workspace = workspace;
    }

    public abstract Version getNewestVersion();

    public final boolean update(DataSource dataSource) {
        return isNewVersionAvailable(dataSource) && tryUpdateFiles(dataSource);
    }

    private boolean isNewVersionAvailable(DataSource dataSource) {
        Version currentVersion = getNewestVersion();
        Version workspaceVersion = dataSource.getMetadata().getVersion();
        return currentVersion.compareTo(workspaceVersion) > 0;
    }

    protected abstract boolean tryUpdateFiles(DataSource dataSource);

    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
