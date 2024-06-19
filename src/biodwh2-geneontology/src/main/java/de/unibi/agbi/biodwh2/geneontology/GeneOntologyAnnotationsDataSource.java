package de.unibi.agbi.biodwh2.geneontology;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyGraphExporter;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyMappingDescriber;
import de.unibi.agbi.biodwh2.geneontology.etl.GeneOntologyUpdater;

public class GeneOntologyAnnotationsDataSource extends DataSource {
    @Override
    public String getId() {
        return "GeneOntologyAnnotations";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getFullName() {
        return "Gene Ontology Annotations (GOA)";
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
    public Updater<GeneOntologyAnnotationsDataSource> getUpdater() {
        return new GeneOntologyUpdater(this);
    }

    @Override
    protected Parser<GeneOntologyAnnotationsDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<GeneOntologyAnnotationsDataSource> getGraphExporter() {
        return new GeneOntologyGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new GeneOntologyMappingDescriber(this);
    }
}
