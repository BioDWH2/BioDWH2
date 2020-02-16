package de.unibi.agbi.biodwh2.drugcentral;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugcentral.etl.*;

import java.util.HashMap;
import java.util.List;

public class DrugCentralDataSource extends DataSource {
    public HashMap<Class, List<Object>> drugCentralDict = new HashMap<>();

    @Override
    public String getId() {
        return "DrugCentral";
    }

    @Override
    public Updater getUpdater() {
        return new DrugCentralUpdater();
    }

    @Override
    public Parser getParser() {
        return new DrugCentralParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new DrugCentralGraphExporter();
    }

    @Override
    public Merger getMerger() {
        return new DrugCentralMerger();
    }
}
