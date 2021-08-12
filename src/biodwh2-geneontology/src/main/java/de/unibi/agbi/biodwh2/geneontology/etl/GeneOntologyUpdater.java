package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

import java.io.IOException;

public class GeneOntologyUpdater extends OBOOntologyUpdater<GeneOntologyDataSource> {
    static final String OBO_FILE_NAME = "go.obo";
    private static final String ANNOTATION_URL_PREFIX = "http://current.geneontology.org/annotations/";
    static final String GOA_HUMAN_FILE_NAME = "goa_human.gaf.gz";
    static final String GOA_HUMAN_COMPLEX_FILE_NAME = "goa_human_complex.gaf.gz";
    static final String GOA_HUMAN_ISOFORM_FILE_NAME = "goa_human_isoform.gaf.gz";
    static final String GOA_HUMAN_RNA_FILE_NAME = "goa_human_rna.gaf.gz";
    private static final String[] ANNOTATION_FILE_NAMES = new String[]{
            GOA_HUMAN_FILE_NAME, GOA_HUMAN_COMPLEX_FILE_NAME, GOA_HUMAN_ISOFORM_FILE_NAME, GOA_HUMAN_RNA_FILE_NAME
    };

    public GeneOntologyUpdater(final GeneOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : ANNOTATION_FILE_NAMES)
            if (!updateAnnotationFile(workspace, fileName))
                return false;
        return super.tryUpdateFiles(workspace);
    }

    private boolean updateAnnotationFile(final Workspace workspace,
                                         final String fileName) throws UpdaterConnectionException {
        final String outputFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
        try {
            HTTPClient.downloadFileAsBrowser(ANNOTATION_URL_PREFIX + fileName, outputFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
        }
        return true;
    }

    @Override
    protected String getDownloadUrl() {
        return "http://current.geneontology.org/ontology/" + OBO_FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = dataVersion.split("releases/")[1].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected String getTargetFileName() {
        return OBO_FILE_NAME;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                OBO_FILE_NAME, GOA_HUMAN_FILE_NAME, GOA_HUMAN_COMPLEX_FILE_NAME, GOA_HUMAN_ISOFORM_FILE_NAME,
                GOA_HUMAN_RNA_FILE_NAME
        };
    }
}
