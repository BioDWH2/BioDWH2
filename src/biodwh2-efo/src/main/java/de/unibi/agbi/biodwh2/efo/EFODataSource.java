package de.unibi.agbi.biodwh2.efo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.OntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.efo.etl.EFOGraphExporter;
import de.unibi.agbi.biodwh2.efo.etl.EFOMappingDescriber;
import de.unibi.agbi.biodwh2.efo.etl.EFOUpdater;

public class EFODataSource extends OntologyDataSource {
    @Override
    public String getId() {
        return "EFO";
    }

    @Override
    public String getLicense() {
        return "Apache-2.0";
    }

    @Override
    public String getFullName() {
        return "Experimental Factor Ontology (EFO)";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new EFOUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new EFOGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new EFOMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
