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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanadianNutrientFileDataSource extends DataSource {
    public Map<String, Yield> yields = new HashMap<>(79);
    public Map<String, Refuse> refuses = new HashMap<>(150);
    public Map<String, Measure> measures = new HashMap<>(1162);
    public Map<String, Nutrient> nutrients = new HashMap<>(152);
    public Map<String, FoodGroup> foodGroups = new HashMap<>(23);
    public Map<String, FoodSource> foodSources = new HashMap<>(16);
    public Map<String, NutrientSource> nutrientSources = new HashMap<>(20);

    public List<YieldAmount> yieldAmounts = new ArrayList<>(1427);
    public List<RefuseAmount> refuseAmounts = new ArrayList<>(6898);
    public List<ConversionFactor> conversionFactors = new ArrayList<>(19506);
    public List<NutrientAmount> nutrientAmounts = new ArrayList<>(524675);

    public Map<String, Food> foods = new HashMap<>(5693);

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
    }
}
