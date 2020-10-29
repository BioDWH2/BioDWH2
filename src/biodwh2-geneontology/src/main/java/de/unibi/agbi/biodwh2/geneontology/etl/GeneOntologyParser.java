package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

public class GeneOntologyParser extends Parser<GeneOntologyDataSource> {
    public GeneOntologyParser(final GeneOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
        return true;
    }
}
