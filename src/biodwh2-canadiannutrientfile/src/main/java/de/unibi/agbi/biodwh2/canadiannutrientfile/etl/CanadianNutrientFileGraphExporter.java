package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.canadiannutrientfile.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;

public class CanadianNutrientFileGraphExporter extends GraphExporter<CanadianNutrientFileDataSource> {
    static final String FOOD_LABEL = "Food";
    static final String FOOD_GROUP_LABEL = "FoodGroup";
    static final String FOOD_SOURCE_LABEL = "FoodSource";
    static final String YIELD_LABEL = "Yield";
    static final String REFUSE_LABEL = "Refuse";
    static final String MEASURE_LABEL = "Measure";
    static final String NUTRIENT_SOURCE_LABEL = "NutrientSource";
    static final String NUTRIENT_LABEL = "Nutrient";

    public CanadianNutrientFileGraphExporter(final CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(FOOD_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(FOOD_LABEL, "code", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(FOOD_GROUP_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(FOOD_GROUP_LABEL, "code", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(FOOD_SOURCE_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(FOOD_SOURCE_LABEL, "code", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(YIELD_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFUSE_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(MEASURE_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(NUTRIENT_SOURCE_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(NUTRIENT_SOURCE_LABEL, "code", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(NUTRIENT_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(NUTRIENT_LABEL, "code", false, IndexDescription.Type.UNIQUE));
        exportFoodGroups(graph);
        exportFoodSources(graph);
        exportFoods(graph);
        exportYields(graph);
        exportRefuses(graph);
        exportMeasures(graph);
        exportNutrientSources(graph);
        exportNutrients(graph);
        exportYieldAmounts(graph);
        exportRefuseAmounts(graph);
        exportConversionFactors(graph);
        exportNutrientAmounts(graph);
        return true;
    }

    private void exportFoodGroups(final Graph graph) {
        for (final FoodGroup foodGroup : dataSource.foodGroups)
            exportFoodGroup(graph, foodGroup);
    }

    private void exportFoodGroup(final Graph graph, final FoodGroup foodGroup) {
        final NodeBuilder builder = graph.buildNode().withLabel(FOOD_GROUP_LABEL);
        builder.withPropertyIfNotNull("id", foodGroup.id);
        builder.withPropertyIfNotNull("code", foodGroup.code);
        builder.withPropertyIfNotNull("name", foodGroup.name);
        builder.withPropertyIfNotNull("name_french", foodGroup.nameFrench);
        builder.build();
    }

    private void exportFoodSources(final Graph graph) {
        for (final FoodSource foodSource : dataSource.foodSources)
            graph.addNode(FOOD_SOURCE_LABEL, "id", foodSource.id, "code", foodSource.code, "description",
                          foodSource.description, "description_french", foodSource.descriptionFrench);
    }

    private void exportFoods(final Graph graph) {
        for (final Food food : dataSource.foods)
            exportFood(graph, food);
    }

    private void exportFood(final Graph graph, final Food food) {
        final NodeBuilder builder = graph.buildNode().withLabel(FOOD_LABEL);
        builder.withPropertyIfNotNull("id", food.id);
        builder.withPropertyIfNotNull("code", food.code);
        builder.withPropertyIfNotNull("description", food.description);
        builder.withPropertyIfNotNull("description_french", food.descriptionFrench);
        builder.withPropertyIfNotNull("country_code", food.countryCode);
        builder.withPropertyIfNotNull("date_of_entry", food.dateOfEntry);
        builder.withPropertyIfNotNull("date_of_publication", food.dateOfPublication);
        builder.withPropertyIfNotNull("scientific_name", food.scientificName);
        final Node node = builder.build();
        graph.addEdge(node, graph.findNode(FOOD_GROUP_LABEL, "id", food.foodGroupID), "PART_OF");
        final Node foodSourceNode = graph.findNode(FOOD_SOURCE_LABEL, "id", food.foodSourceID);
        if (foodSourceNode != null)
            graph.addEdge(node, foodSourceNode, "HAS_SOURCE");
    }

    private void exportYields(final Graph graph) {
        for (final Yield yield : dataSource.yields)
            graph.addNode(YIELD_LABEL, "id", yield.id, "description", yield.description, "description_french",
                          yield.descriptionFrench);
    }

    private void exportRefuses(final Graph graph) {
        for (final Refuse refuse : dataSource.refuses)
            graph.addNode(REFUSE_LABEL, "id", refuse.id, "description", refuse.description, "description_french",
                          refuse.descriptionFrench);
    }

    private void exportMeasures(final Graph graph) {
        for (final Measure measure : dataSource.measures)
            graph.addNode(MEASURE_LABEL, "id", measure.id, "description", measure.description, "description_french",
                          measure.descriptionFrench);
    }

    private void exportNutrientSources(final Graph graph) {
        for (final NutrientSource source : dataSource.nutrientSources)
            graph.addNode(NUTRIENT_SOURCE_LABEL, "id", source.id, "code", source.code, "description",
                          source.description, "description_french", source.descriptionFrench);
    }

    private void exportNutrients(final Graph graph) {
        for (final Nutrient nutrient : dataSource.nutrients) {
            Node nutrientNode = graph.addNode(NUTRIENT_LABEL);
            nutrientNode.setProperty("id", nutrient.id);
            nutrientNode.setProperty("code", nutrient.code);
            nutrientNode.setProperty("symbol", nutrient.symbol);
            nutrientNode.setProperty("unit", nutrient.unit);
            nutrientNode.setProperty("name", nutrient.name);
            nutrientNode.setProperty("name_french", nutrient.nameFrench);
            nutrientNode.setProperty("infoods_tag", nutrient.tagName);
            nutrientNode.setProperty("decimals_of_value", nutrient.decimals);
            graph.update(nutrientNode);
        }
    }

    private void exportYieldAmounts(final Graph graph) {
        for (final YieldAmount amount : dataSource.yieldAmounts)
            graph.addEdge(graph.findNode(FOOD_LABEL, "id", amount.foodID),
                          graph.findNode(YIELD_LABEL, "id", amount.yieldID), "HAS_YIELD", "amount", amount.amount,
                          "date_of_entry", amount.dateOfEntry);
    }

    private void exportRefuseAmounts(final Graph graph) {
        for (final RefuseAmount amount : dataSource.refuseAmounts)
            graph.addEdge(graph.findNode(FOOD_LABEL, "id", amount.foodID),
                          graph.findNode(REFUSE_LABEL, "id", amount.refuseID), "HAS_REFUSE", "amount", amount.amount,
                          "date_of_entry", amount.dateOfEntry);
    }

    private void exportConversionFactors(final Graph graph) {
        for (ConversionFactor factor : dataSource.conversionFactors) {
            final Node measureNode = graph.findNode(MEASURE_LABEL, "id", factor.measureID);
            if (measureNode != null)
                graph.addEdge(graph.findNode(FOOD_LABEL, "id", factor.foodID), measureNode, "HAS_MEASURE",
                              "conversion_factor", factor.conversionFactorValue, "date_of_entry", factor.dateOfEntry);
        }
    }

    private void exportNutrientAmounts(final Graph graph) {
        for (final NutrientAmount amount : dataSource.nutrientAmounts) {
            final Node node = graph.addNode("NutrientAmount", "nutrient_value", amount.value, "standard_error",
                                            amount.standardError, "number_of_observations", amount.numberOfObservations,
                                            "date_of_entry", amount.dateOfEntry);
            graph.addEdge(graph.findNode(FOOD_LABEL, "id", amount.foodID), node, "CONTAINS");
            final Node nutrientNode = graph.findNode(NUTRIENT_LABEL, "id", amount.nutrientID);
            if (nutrientNode != null)
                graph.addEdge(node, nutrientNode, "OF_TYPE");
            graph.addEdge(node, graph.findNode(NUTRIENT_SOURCE_LABEL, "id", amount.nutrientSourceID), "HAS_SOURCE");
        }
    }
}
