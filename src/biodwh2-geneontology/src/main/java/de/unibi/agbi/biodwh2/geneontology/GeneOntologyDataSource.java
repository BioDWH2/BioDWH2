package de.unibi.agbi.biodwh2.geneontology;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyGraphExporter;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyParser;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyUpdater;

public class GeneOntologyDataSource extends DataSource {
    @Override
    public String getId() {
        return "GeneOntology";
    }

    @Override
    public Updater getUpdater() {
        return new GeneOntologyUpdater();
    }

    @Override
    protected Parser getParser() {
        return new GeneOntologyParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new GeneOntologyGraphExporter();
    }
}
