package de.unibi.agbi.biodwh2.gene2phenotype;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gen2PhenotypeUpdater;

public class Gen2PhenotypeDataSource  extends DataSource {

    @Override
    public String getId() {
        return "Gen2Phenotype";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new Gen2PhenotypeUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return null;
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return null;
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return null;
    }

    @Override
    protected void unloadData() {

    }
}
