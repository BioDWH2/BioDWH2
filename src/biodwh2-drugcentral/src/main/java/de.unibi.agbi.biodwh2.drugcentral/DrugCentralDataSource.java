package de.unibi.agbi.biodwh2.drugcentral;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugcentral.etl.*;

import java.util.Map;

public class DrugCentralDataSource extends DataSource {
    @Override
    public String getId() {
        return "DrugCentral";
    }

    @Override
    public String getFullName() {
        return "DrugCentral";
    }

    @Override
    public String getDescription() {
        return "DrugCentral is online drug information resource created and maintained by Division of Translational " +
               "Informatics at University of New Mexico in collaboration with the IDG.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<DrugCentralDataSource> getUpdater() {
        return new DrugCentralUpdater(this);
    }

    @Override
    public Parser<DrugCentralDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    public GraphExporter<DrugCentralDataSource> getGraphExporter() {
        return new DrugCentralGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DrugCentralMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("skipLINCSSignatures", DataSourcePropertyType.BOOLEAN);
        result.put("skipFAERSReports", DataSourcePropertyType.BOOLEAN);
        result.put("skipDrugLabelFullTexts", DataSourcePropertyType.BOOLEAN);
        return result;
    }
}
