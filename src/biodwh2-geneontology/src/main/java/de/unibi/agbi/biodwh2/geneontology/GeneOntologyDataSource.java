package de.unibi.agbi.biodwh2.geneontology;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class GeneOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final String OBO_FILE_NAME = "go.obo";

    @Override
    public String getId() {
        return "GeneOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getFullName() {
        return "Gene Ontology (GO)";
    }

    @Override
    public String getDescription() {
        return "The GO knowledgebase is the world's largest source of information on the functions of genes.";
    }

    @Override
    protected String getDownloadUrl() {
        return "http://current.geneontology.org/ontology/" + OBO_FILE_NAME;
    }

    @Override
    protected String getTargetFileName() {
        return OBO_FILE_NAME;
    }

    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = dataVersion.split("releases/")[1].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    public String getIdPrefix() {
        return "GO";
    }
}
