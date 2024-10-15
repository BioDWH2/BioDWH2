package de.unibi.agbi.biodwh2.iptmnet.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.iptmnet.IPTMNetDataSource;

import java.io.IOException;

public class IPTMNetUpdater extends Updater<IPTMNetDataSource> {
    private static final String VERSION_URL = "https://research.bioinformatics.udel.edu/iptmnet_test/static/iptmnet/client/statistics.json";
    private static final String DOWNLOAD_URL_PREFIX = "https://research.bioinformatics.udel.edu/iptmnet_data/files/current/";
    static final String PTM_FILE_NAME = "ptm.txt";
    static final String SCORE_FILE_NAME = "score.txt";
    static final String PROTEIN_FILE_NAME = "protein.txt";

    public IPTMNetUpdater(final IPTMNetDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final var root = mapper.readTree(source);
            return Version.tryParse(root.get("release").asText());
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + PTM_FILE_NAME, PTM_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + SCORE_FILE_NAME, SCORE_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + PROTEIN_FILE_NAME, PROTEIN_FILE_NAME);
        return true;
    }
}
