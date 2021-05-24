package de.unibi.agbi.biodwh2.gene2phenotype;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gen2PhenotypeGraphExporter;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gen2PhenotypeMappingDescriber;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gen2PhenotypeParser;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gen2PhenotypeUpdater;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;

import java.util.ArrayList;
import java.util.List;

public class Gen2PhenotypeDataSource  extends DataSource {
    public List<GeneDiseasePair> geneDiseasePairs = new ArrayList<>(300);

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
        return new Gen2PhenotypeParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new Gen2PhenotypeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Gen2PhenotypeMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        this.geneDiseasePairs = null;
    }
}
