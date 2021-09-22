package de.unibi.agbi.biodwh2.gene2phenotype;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeGraphExporter;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeMappingDescriber;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeParser;
import de.unibi.agbi.biodwh2.gene2phenotype.etl.Gene2PhenotypeUpdater;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;

import java.util.ArrayList;
import java.util.List;

public class Gene2PhenotypeDataSource extends DataSource {
    public final List<GeneDiseasePair> geneDiseasePairs = new ArrayList<>();

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
        return new Gene2PhenotypeParser(this);
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
        geneDiseasePairs.clear();
    }
}
