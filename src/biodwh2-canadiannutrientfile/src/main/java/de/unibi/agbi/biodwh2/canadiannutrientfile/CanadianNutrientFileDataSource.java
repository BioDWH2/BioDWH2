package de.unibi.agbi.biodwh2.canadiannutrientfile;

import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileGraphExporter;
import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileMappingDescriber;
import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileParser;
import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

public class CanadianNutrientFileDataSource extends DataSource {
    @Override
    public String getId() {
        return "CanadianNutrientFile";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new CanadianNutrientFileUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new CanadianNutrientFileParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new CanadianNutrientFileGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new CanadianNutrientFileMappingDescriber(this);
    }

    @Override
    protected void unloadData() {

    }
}
