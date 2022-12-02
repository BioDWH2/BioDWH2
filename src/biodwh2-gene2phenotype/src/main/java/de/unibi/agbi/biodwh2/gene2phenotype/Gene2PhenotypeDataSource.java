package de.unibi.agbi.biodwh2.gene2phenotype;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeGraphExporter;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeMappingDescriber;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeUpdater;

public class Gene2PhenotypeDataSource extends DataSource {
    @Override
    public String getId() {
        return "Gene2Phenotype";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new Gene2PhenotypeUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new Gene2PhenotypeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Gene2PhenotypeMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
