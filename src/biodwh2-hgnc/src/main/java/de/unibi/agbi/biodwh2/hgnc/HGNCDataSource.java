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
    public String getFullName() {
        return "HUGO Gene Nomenclature Committee (HGNC)";
    }

    @Override
    public String getDescription() {
        return "The HGNC sets the standards for human gene nomenclature and approves a unique and meaningful name " +
               "for every known human gene, based on a query of experts.";
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
