package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

public class GeneOntologyUpdater extends OBOOntologyUpdater<GeneOntologyDataSource> {
    public GeneOntologyUpdater(GeneOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getDownloadUrl() {
        return "http://current.geneontology.org/ontology/go.obo";
    }

    @Override
    protected Version getVersionFromDataVersionLine(String dataVersion) {
        final String[] versionParts = dataVersion.split("releases/")[1].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected String getTargetFileName() {
        return "go.obo";
    }
}
