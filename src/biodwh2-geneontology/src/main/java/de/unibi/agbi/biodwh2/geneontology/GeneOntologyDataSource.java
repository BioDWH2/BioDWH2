package de.unibi.agbi.biodwh2.geneontology;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyGraphExporter;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyMappingDescriber;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyParser;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyUpdater;

public class GeneOntologyDataSource extends DataSource {
    @Override
    public String getId() {
        return "GeneOntology";
    }

    @Override
    public Updater<GeneOntologyDataSource> getUpdater() {
        return new GeneOntologyUpdater(this);
    }

    @Override
    protected Parser<GeneOntologyDataSource> getParser() {
        return new GeneOntologyParser(this);
    }

    @Override
    protected GraphExporter<GeneOntologyDataSource> getGraphExporter() {
        return new GeneOntologyGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new GeneOntologyMappingDescriber();
    }

    @Override
    protected void unloadData() {
    }
}
