package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;

public abstract class SingleFileFTPWebUpdater<D extends DataSource> extends Updater<D> {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            String source = HTTPClient.getWebsiteSource(getFTPIndexUrl());
            String searchKey = "<a href=\"" + getFileName() + "\">";
            if (!source.contains(searchKey))
                return null;
            String date = source.split(searchKey)[1].split("<td")[1].split("[<>]")[1].split(" ")[0];
            String[] dateParts = date.split("-");
            return new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                               Integer.parseInt(dateParts[2]));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, D dataSource) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(getFTPIndexUrl() + getFileName(),
                                             dataSource.resolveSourceFilePath(workspace, getFileName()));
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    protected abstract String getFTPIndexUrl();

    protected abstract String getFileName();
}
