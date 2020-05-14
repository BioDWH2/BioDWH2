package de.unibi.agbi.biodwh2.dgidb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.dgidb.etl.*;
import de.unibi.agbi.biodwh2.dgidb.model.Category;
import de.unibi.agbi.biodwh2.dgidb.model.Drug;
import de.unibi.agbi.biodwh2.dgidb.model.Gene;
import de.unibi.agbi.biodwh2.dgidb.model.Interaction;

import java.util.List;

public class DGIdbDataSource extends DataSource {
    public List<Drug> drugs;
    public List<Category> categories;
    public List<Interaction> interactions;
    public List<Gene> genes;

    @Override
    public String getId() {
        return "DGIdb";
    }

    @Override
    public Updater<DGIdbDataSource> getUpdater() {
        return new DGIdbUpdater();
    }

    @Override
    public Parser<DGIdbDataSource> getParser() {
        return new DGIdbParser();
    }

    @Override
    public RDFExporter<DGIdbDataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<DGIdbDataSource> getGraphExporter() {
        return new DGIdbGraphExporter();
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DGIdbMappingDescriber();
    }

    @Override
    protected void unloadData() {
        drugs = null;
        categories = null;
        interactions = null;
        genes = null;
    }
}
