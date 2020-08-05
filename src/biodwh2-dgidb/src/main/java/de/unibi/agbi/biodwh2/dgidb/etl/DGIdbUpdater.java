package de.unibi.agbi.biodwh2.dgidb.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class DGIdbUpdater extends Updater<DGIdbDataSource> {
    public DGIdbUpdater(DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        String source;
        try {
            source = HTTPClient.getWebsiteSource("https://api.github.com/repos/griffithlab/dgi-db/releases/latest");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        JsonNode json = parseJsonVersionSource(source);
        String version = json.get("tag_name").asText().replace("v", "");
        return parseVersion(version);
    }

    private JsonNode parseJsonVersionSource(String source) throws UpdaterMalformedVersionException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(source);
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    private Version parseVersion(String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFile(workspace, dataSource, "http://www.dgidb.org/data/interactions.tsv");
        downloadFile(workspace, dataSource, "http://www.dgidb.org/data/drugs.tsv");
        downloadFile(workspace, dataSource, "http://www.dgidb.org/data/genes.tsv");
        downloadFile(workspace, dataSource, "http://www.dgidb.org/data/categories.tsv");
        return true;
    }

    private void downloadFile(final Workspace workspace, final DataSource dataSource,
                              final String url) throws UpdaterException {
        try {
            String fileName = url.substring(StringUtils.lastIndexOf(url, "/") + 1);
            HTTPClient.downloadFileAsBrowser(url, dataSource.resolveSourceFilePath(workspace, fileName));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file \"" + url + "\"", e);
        }
    }
}
