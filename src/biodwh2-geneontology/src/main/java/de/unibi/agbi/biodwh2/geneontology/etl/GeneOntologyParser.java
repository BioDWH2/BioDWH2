package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

public class GeneOntologyParser extends Parser<GeneOntologyDataSource> {
    @Override
    public boolean parse(Workspace workspace, GeneOntologyDataSource dataSource) throws ParserException {
        return false;
    }
}
