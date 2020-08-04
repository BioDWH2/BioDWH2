package de.unibi.agbi.biodwh2.kegg;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.kegg.etl.KeggGraphExporter;
import de.unibi.agbi.biodwh2.kegg.etl.KeggMappingDescriber;
import de.unibi.agbi.biodwh2.kegg.etl.KeggParser;
import de.unibi.agbi.biodwh2.kegg.etl.KeggUpdater;
import de.unibi.agbi.biodwh2.kegg.model.*;

import java.util.List;

public class KeggDataSource extends DataSource {
    public List<DrugGroup> drugGroups;
    public List<Disease> diseases;
    public List<Drug> drugs;
    public List<Network> networks;
    public List<Variant> variants;

    @Override
    public String getId() {
        return "KEGG";
    }

    @Override
    public Updater<KeggDataSource> getUpdater() {
        return new KeggUpdater();
    }

    @Override
    protected Parser<KeggDataSource> getParser() {
        return new KeggParser();
    }

    @Override
    protected GraphExporter<KeggDataSource> getGraphExporter() {
        return new KeggGraphExporter();
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new KeggMappingDescriber();
    }

    @Override
    protected void unloadData() {
        drugGroups = null;
        diseases = null;
        drugs = null;
        networks = null;
        variants = null;
    }
}
