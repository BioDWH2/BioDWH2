package de.unibi.agbi.biodwh2.t3db.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.t3db.T3DBDataSource;

import java.io.IOException;

public class T3DBUpdater extends Updater<T3DBDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "http://www.t3db.ca/system/downloads/current/";
    static final String TOXINS_XML_FILE_NAME = "toxins.xml.zip";
    static final String TARGETS_XML_FILE_NAME = "targets.xml.zip";
    static final String TOXINS_CSV_FILE_NAME = "toxins.csv.zip";
    static final String TARGETS_CSV_FILE_NAME = "targets.csv.zip";
    static final String MOAS_CSV_FILE_NAME = "moas.csv.zip";
    static final String TOXIN_STRUCTURES_FILE_NAME = "structures.zip";
    //static final String PROTEIN_SEQUENCES_FILE_NAME = "sequences/protein/target_protein_sequences.fasta.zip";
    //static final String GENE_SEQUENCES_FILE_NAME = "sequences/gene/target_gene_sequences.fasta.zip";
    private static final String[] FILE_NAMES = new String[]{
            TOXINS_XML_FILE_NAME, TARGETS_XML_FILE_NAME, TOXINS_CSV_FILE_NAME, TARGETS_CSV_FILE_NAME,
            MOAS_CSV_FILE_NAME, TOXIN_STRUCTURES_FILE_NAME
    };

    public T3DBUpdater(final T3DBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source;
        try {
            source = HTTPClient.getWebsiteSource("http://www.t3db.ca/downloads");
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException("", e);
        }
        final String versionPrefix = "T3DB Version <strong>";
        final int index = source.indexOf(versionPrefix);
        if (index != -1) {
            final int endIndex = source.indexOf("</strong>", index + versionPrefix.length());
            if (endIndex != -1) {
                final String versionText = source.substring(index + versionPrefix.length(), endIndex);
                return Version.tryParse(versionText);
            }
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES) {
            try {
                HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL_PREFIX + fileName,
                                                 dataSource.resolveSourceFilePath(workspace, fileName));
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
            }
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return FILE_NAMES;
    }
}
