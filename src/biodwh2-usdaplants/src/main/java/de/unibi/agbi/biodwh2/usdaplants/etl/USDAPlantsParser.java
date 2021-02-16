package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;
import de.unibi.agbi.biodwh2.usdaplants.model.Plant;

import java.util.List;

public class USDAPlantsParser extends SingleFileCsvParser<USDAPlantsDataSource, Plant> {
    public USDAPlantsParser(final USDAPlantsDataSource dataSource) {
        super(dataSource, Plant.class, true, CsvType.CSV, USDAPlantsUpdater.PLANT_LIST_FILE_NAME);
    }

    @Override
    protected void storeResults(final USDAPlantsDataSource dataSource, final List<Plant> results) {
        dataSource.plants = results;
    }
}
