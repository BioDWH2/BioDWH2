package de.unibi.agbi.biodwh2.hpo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hpo.etl.HPOGraphExporter;
import de.unibi.agbi.biodwh2.hpo.etl.HPOMappingDescriber;
import de.unibi.agbi.biodwh2.hpo.etl.HPOParser;
import de.unibi.agbi.biodwh2.hpo.etl.HPOUpdater;

public class HPODataSource extends DataSource {
    @Override
    public String getId() {
        return "HPO";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<HPODataSource> getUpdater() {
        return new HPOUpdater(this);
    }

    @Override
    protected Parser<HPODataSource> getParser() {
        return new HPOParser(this);
    }

    @Override
    protected GraphExporter<HPODataSource> getGraphExporter() {
        return new HPOGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HPOMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
