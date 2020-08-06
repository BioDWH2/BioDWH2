package de.unibi.agbi.biodwh2.hgnc;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hgnc.etl.*;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.util.List;

public class HGNCDataSource extends DataSource {
    public List<Gene> genes;

    @Override
    public String getId() {
        return "HGNC";
    }

    @Override
    public Updater<HGNCDataSource> getUpdater() {
        return new HGNCUpdater(this);
    }

    @Override
    public Parser<HGNCDataSource> getParser() {
        return new HGNCParser(this);
    }

    @Override
    public GraphExporter<HGNCDataSource> getGraphExporter() {
        return new HGNCGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HGNCMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        genes.clear();
    }
}
