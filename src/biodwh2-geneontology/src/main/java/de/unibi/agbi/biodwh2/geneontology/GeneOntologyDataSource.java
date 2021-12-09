package de.unibi.agbi.biodwh2.geneontology;

import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.OntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyGraphExporter;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyMappingDescriber;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyUpdater;

public class GeneOntologyDataSource extends OntologyDataSource {
    @Override
    public String getId() {
        return "GeneOntology";
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
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
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
