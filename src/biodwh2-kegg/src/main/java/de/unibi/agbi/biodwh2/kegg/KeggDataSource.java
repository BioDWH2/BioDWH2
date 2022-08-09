package de.unibi.agbi.biodwh2.kegg;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
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
    public String getFullName() {
        return "Kyoto Encyclopedia of Genes and Genomes (KEGG)";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<KeggDataSource> getUpdater() {
        return new KeggUpdater(this);
    }

    @Override
    protected Parser<KeggDataSource> getParser() {
        return new KeggParser(this);
    }

    @Override
    protected GraphExporter<KeggDataSource> getGraphExporter() {
        return new KeggGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new KeggMappingDescriber(this);
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
