package de.unibi.agbi.biodwh2.kegg;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.kegg.etl.KeggGraphExporter;
import de.unibi.agbi.biodwh2.kegg.etl.KeggMerger;
import de.unibi.agbi.biodwh2.kegg.etl.KeggParser;
import de.unibi.agbi.biodwh2.kegg.etl.KeggUpdater;
import de.unibi.agbi.biodwh2.kegg.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeggDataSource extends DataSource {
    public Map<String, Set<String>> drugGroupChildMap;
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
    public Updater getUpdater() {
        return new KeggUpdater();
    }

    @Override
    protected Parser getParser() {
        return new KeggParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new KeggGraphExporter();
    }

    @Override
    public Merger getMerger() {
        return new KeggMerger();
    }
}
