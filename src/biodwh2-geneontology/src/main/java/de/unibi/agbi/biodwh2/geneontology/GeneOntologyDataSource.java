package de.unibi.agbi.biodwh2.geneontology;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyGraphExporter;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyMappingDescriber;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyUpdater;

public class GeneOntologyDataSource extends DataSource {
    @Override
    public String getId() {
        return "GeneOntology";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<GeneOntologyDataSource> getUpdater() {
        return new GeneOntologyUpdater(this);
    }

    @Override
    protected Parser<GeneOntologyDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<GeneOntologyDataSource> getGraphExporter() {
        return new GeneOntologyGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new GeneOntologyMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
