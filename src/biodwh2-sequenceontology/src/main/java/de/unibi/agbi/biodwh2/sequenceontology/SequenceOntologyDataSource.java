package de.unibi.agbi.biodwh2.sequenceontology;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.OntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.sequenceontology.etl.SequenceOntologyGraphExporter;
import de.unibi.agbi.biodwh2.sequenceontology.etl.SequenceOntologyMappingDescriber;
import de.unibi.agbi.biodwh2.sequenceontology.etl.SequenceOntologyUpdater;

public class SequenceOntologyDataSource extends OntologyDataSource {
    @Override
    public String getId() {
        return "SequenceOntology";
    }

    @Override
    public String getLicense() {
        return "CC BY-SA 4.0";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new SequenceOntologyUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new SequenceOntologyGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new SequenceOntologyMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
