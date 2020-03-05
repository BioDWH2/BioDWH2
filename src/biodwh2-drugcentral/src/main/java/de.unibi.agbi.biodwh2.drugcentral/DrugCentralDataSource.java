package de.unibi.agbi.biodwh2.drugcentral;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugcentral.etl.*;

import java.util.HashMap;
import java.util.List;

public class DrugCentralDataSource extends DataSource {
    public HashMap<Class<?>, List<?>> drugCentralDict = new HashMap<>();

    @Override
    public String getId() {
        return "DrugCentral";
    }

    @Override
    public Updater<DrugCentralDataSource> getUpdater() {
        return new DrugCentralUpdater();
    }

    @Override
    public Parser<DrugCentralDataSource> getParser() {
        return new DrugCentralParser();
    }

    @Override
    public RDFExporter<DrugCentralDataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<DrugCentralDataSource> getGraphExporter() {
        return new DrugCentralGraphExporter();
    }
}
