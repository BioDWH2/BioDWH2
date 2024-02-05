package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

import java.io.IOException;

public class HPOUpdater extends OBOOntologyUpdater<HPODataSource> {
    public static final String ANNOTATIONS_FILE_NAME = "phenotype.hpoa";
    public static final String GENES_TO_PHENOTYPE_FILE_NAME = "genes_to_phenotype.txt";
    public static final String PHENOTYPE_TO_GENES_FILE_NAME = "phenotype_to_genes.txt";
    public static final String PHENOTYPES_FILE_NAME = "hp.obo";
    private static final String CURRENT_VERSION_URL =
            "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/" + PHENOTYPES_FILE_NAME;
    private static final String ANNOTATIONS_URL = "http://purl.obolibrary.org/obo/hp/hpoa/" + ANNOTATIONS_FILE_NAME;
    private static final String GENES_TO_PHENOTYPES_URL =
            "http://purl.obolibrary.org/obo/hp/hpoa/" + GENES_TO_PHENOTYPE_FILE_NAME;
    private static final String PHENOTYPES_TO_GENES_URL =
            "http://purl.obolibrary.org/obo/hp/hpoa/" + PHENOTYPE_TO_GENES_FILE_NAME;

    public HPOUpdater(final HPODataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getDownloadUrl() {
        return CURRENT_VERSION_URL;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = dataVersion.split("hp/releases/")[1].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected String getTargetFileName() {
        return PHENOTYPES_FILE_NAME;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        updateFile(workspace, ANNOTATIONS_URL, ANNOTATIONS_FILE_NAME);
        updateFile(workspace, GENES_TO_PHENOTYPES_URL, GENES_TO_PHENOTYPE_FILE_NAME);
        updateFile(workspace, PHENOTYPES_TO_GENES_URL, PHENOTYPE_TO_GENES_FILE_NAME);
        return super.tryUpdateFiles(workspace);
    }

    private void updateFile(final Workspace workspace, final String url,
                            final String fileName) throws UpdaterConnectionException {
        final String targetFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
        try {
            HTTPClient.downloadFileAsBrowser(url, targetFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + url + "'", e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                ANNOTATIONS_FILE_NAME, GENES_TO_PHENOTYPE_FILE_NAME, PHENOTYPE_TO_GENES_FILE_NAME, PHENOTYPES_FILE_NAME
        };
    }
}
