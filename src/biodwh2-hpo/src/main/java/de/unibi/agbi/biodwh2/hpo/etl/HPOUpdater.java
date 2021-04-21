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
    private static final String CURRENT_VERSION_URL = "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    private static final String ANNOTATIONS_URL = "http://purl.obolibrary.org/obo/hp/hpoa/phenotype.hpoa";
    private static final String GENES_TO_PHENOTYPES_URL = "http://purl.obolibrary.org/obo/hp/hpoa/genes_to_phenotype.txt";
    private static final String PHENOTYPES_TO_GENES_URL = "http://purl.obolibrary.org/obo/hp/hpoa/phenotype_to_genes.txt";

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
        return "hp.obo";
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            updateFile(workspace, ANNOTATIONS_URL, "phenotype.hpoa");
            updateFile(workspace, GENES_TO_PHENOTYPES_URL, "genes_to_phenotype.txt");
            updateFile(workspace, PHENOTYPES_TO_GENES_URL, "phenotype_to_genes.txt");
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download HPO annotations", e);
        }
        return super.tryUpdateFiles(workspace);
    }

    private void updateFile(final Workspace workspace, final String url, final String fileName) throws IOException {
        final String targetFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
        HTTPClient.downloadFileAsBrowser(url, targetFilePath);
    }
}
