package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.hpo.HPOAnnotationsDataSource;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

import java.io.IOException;

public class HPOUpdater extends Updater<HPOAnnotationsDataSource> {
    public static final String ANNOTATIONS_FILE_NAME = "phenotype.hpoa";
    public static final String GENES_TO_PHENOTYPE_FILE_NAME = "genes_to_phenotype.txt";
    public static final String PHENOTYPE_TO_GENES_FILE_NAME = "phenotype_to_genes.txt";
    private static final String DOWNLOAD_URL_PREFIX = "http://purl.obolibrary.org/obo/hp/hpoa/";

    public HPOUpdater(final HPOAnnotationsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            return OBOOntologyUpdater.getVersionFromOBOUrl(HPODataSource.DOWNLOAD_URL,
                                                           HPODataSource::versionFromDataVersionLine);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version number", e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + ANNOTATIONS_FILE_NAME, ANNOTATIONS_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + GENES_TO_PHENOTYPE_FILE_NAME,
                              GENES_TO_PHENOTYPE_FILE_NAME);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + PHENOTYPE_TO_GENES_FILE_NAME,
                              PHENOTYPE_TO_GENES_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{ANNOTATIONS_FILE_NAME, GENES_TO_PHENOTYPE_FILE_NAME, PHENOTYPE_TO_GENES_FILE_NAME};
    }
}
