package de.unibi.agbi.biodwh2.hgnc.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

public class HGNCUpdater extends Updater<HGNCDataSource> {
    private static final String VERSION_URL = "https://rest.genenames.org/info";
    private static final String DOWNLOAD_URL_PREFIX = "https://storage.googleapis.com/public-download-files/hgnc/tsv/tsv/";
    static final String FILE_NAME = "hgnc_complete_set.txt";

    public HGNCUpdater(HGNCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(Workspace workspace) throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource(VERSION_URL, Map.of("Accept", "application/json"));
            ObjectMapper mapper = new ObjectMapper();
            final var root = mapper.readTree(source);
            final var lastModifiedNode = root.get("lastModified");
            final var dateTime = OffsetDateTime.parse(lastModifiedNode.asText());
            return new Version(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
        } catch (IOException e) {
            throw new UpdaterException("Failed to retrieve version", e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + FILE_NAME, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
