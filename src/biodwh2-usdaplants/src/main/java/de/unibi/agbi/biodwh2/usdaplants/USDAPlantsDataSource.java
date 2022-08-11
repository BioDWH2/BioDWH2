package de.unibi.agbi.biodwh2.usdaplants;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.usdaplants.etl.USDAPlantsGraphExporter;
import de.unibi.agbi.biodwh2.usdaplants.etl.USDAPlantsMappingDescriber;
import de.unibi.agbi.biodwh2.usdaplants.etl.USDAPlantsParser;
import de.unibi.agbi.biodwh2.usdaplants.etl.USDAPlantsUpdater;
import de.unibi.agbi.biodwh2.usdaplants.model.Plant;

import java.util.List;

public class USDAPlantsDataSource extends DataSource {
    public List<Plant> plants;

    @Override
    public String getId() {
        return "USDA-PLANTS";
    }

    @Override
    public String getFullName() {
        return "USDA PLANTS";
    }

    @Override
    public String getLicense() {
        return "Free with attribution";
    }

    @Override
    public String getDescription() {
        return "The PLANTS Database provides standardized information about the vascular plants, mosses, liverworts, " +
               "hornworts, and lichens of the U.S. and its territories.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new USDAPlantsUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new USDAPlantsParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new USDAPlantsGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new USDAPlantsMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        plants = null;
    }
}
