package de.unibi.agbi.biodwh2.canadiannutrientfile;

import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileGraphExporter;
import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileMappingDescriber;
import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileParser;
import de.unibi.agbi.biodwh2.canadiannutrientfile.etl.CanadianNutrientFileUpdater;
import de.unibi.agbi.biodwh2.canadiannutrientfile.model.*;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

import java.util.ArrayList;
import java.util.List;

public class CanadianNutrientFileDataSource extends DataSource {
    public List<Yield> yields = new ArrayList<>();
    public List<Refuse> refuses = new ArrayList<>();
    public List<Measure> measures = new ArrayList<>();
    public List<Nutrient> nutrients = new ArrayList<>();
    public List<FoodGroup> foodGroups = new ArrayList<>();
    public List<FoodSource> foodSources = new ArrayList<>();
    public List<NutrientSource> nutrientSources = new ArrayList<>();
    public List<YieldAmount> yieldAmounts = new ArrayList<>();
    public List<RefuseAmount> refuseAmounts = new ArrayList<>();
    public List<ConversionFactor> conversionFactors = new ArrayList<>();
    public List<NutrientAmount> nutrientAmounts = new ArrayList<>();
    public List<Food> foods = new ArrayList<>();

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
        yields = null;
        refuses = null;
        measures = null;
        nutrients = null;
        foodGroups = null;
        foodSources = null;
        nutrientSources = null;
        yieldAmounts = null;
        refuseAmounts = null;
        conversionFactors = null;
        nutrientAmounts = null;
        foods = null;
    }
}
