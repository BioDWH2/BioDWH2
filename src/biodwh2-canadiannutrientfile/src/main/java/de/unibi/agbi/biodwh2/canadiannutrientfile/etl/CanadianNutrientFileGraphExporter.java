package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.canadiannutrientfile.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CanadianNutrientFileGraphExporter extends GraphExporter<CanadianNutrientFileDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CanadianNutrientFileParser.class);

    Map<String, Node> foodNodes = new HashMap<>(5700);
    Map<String, Node> foodGroupNodes = new HashMap<>(25);
    Map<String, Node> foodSourceNodes = new HashMap<>(20);
    Map<String, Node> yieldNodes = new HashMap<>(80);
    Map<String, Node> refuseNodes = new HashMap<>(150);
    Map<String, Node> measureNodes = new HashMap<>(1165);

    Map<String, Node> nutrientSourceNodes = new HashMap<>(20);
    Map<String, Node> nutrientNodes = new HashMap<>(155);

    public CanadianNutrientFileGraphExporter(CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        /*graph.setNodeIndexPropertyKeys("food_code",
                                       //because the source called this "identifier" and the food_id is only for the Data Base
                                       "food_group_code",      //same as food_code
                                       "food_source_code",     //same as food_code
                                       "yield_id", "refuse_id", "measure_id", "nutrient_source_code",
                                       //same as food_code
                                       "nutrient_code"         //same as food_code
        );*/

        loadNodes(graph);
        LOGGER.debug("load " + foodNodes.size() + " foods");

        addEdges(graph);
        LOGGER.debug("load " + nutrientNodes.size() + "with " + dataSource.nutrientAmounts.size() + " connections");

        return true;
    }

    /**
     * generates for every food and nutrient amount the edges. Also annotates the edges if needed.
     *
     * @param graph the graph given by the main program.
     */
    private void addEdges(Graph graph) {
        for (Food f : dataSource.foods.values()) {
            graph.addEdge(foodNodes.get(f.getId()), foodGroupNodes.get(f.getFoodGroupID()), "IS_IN");
            try {
                graph.addEdge(foodNodes.get(f.getId()), foodSourceNodes.get(f.getFoodSourceID()), "COMES_FROM");
            } catch (GraphCacheException e) {
                LOGGER.warn("Food source " + f.getFoodSourceID() + " for food " + f.getId() + " not found. Ignoring.");
            }

            for (YieldAmount ya : dataSource.yieldAmounts) {
                if (ya.getFoodID().equals(f.getId())) {
                    Map<String, Object> properties = new HashMap<>(2);
                    properties.put("yield_amount", ya.getAmount());
                    properties.put("date_of_entry", ya.getDateOfEntry());
                    graph.addEdge(foodNodes.get(f.getId()), yieldNodes.get(ya.getYieldID()), "has_yield", properties);
                }
            }

            for (RefuseAmount ra : dataSource.refuseAmounts) {
                if (ra.getFoodID().equals(f.getId())) {
                    Map<String, Object> properties = new HashMap<>(2);
                    properties.put("refuse_amount", ra.getAmount());
                    properties.put("date_of_entry", ra.getDateOfEntry());
                    graph.addEdge(foodNodes.get(f.getId()), refuseNodes.get(ra.getRefuseID()), "has_refuse",
                                  properties);
                }
            }

            for (ConversionFactor cf : dataSource.conversionFactors) {
                if (cf.getFoodID().equals(f.getId())) {
                    Map<String, Object> properties = new HashMap<>(2);
                    properties.put("conversion_factor", cf.getConversionValue());
                    properties.put("date_of_entry", cf.getDateOfEntry());
                    try {
                        graph.addEdge(foodNodes.get(f.getId()), measureNodes.get(cf.getMeasureID()), "HAS_MEASURE",
                                      properties);
                    } catch (GraphCacheException e) {
                        LOGGER.warn("Measurement with id " + cf.getMeasureID() + " for food " + f.getId() +
                                    " not found. Ignoring.");
                    }
                }
            }
        }

        for (NutrientAmount na : dataSource.nutrientAmounts) {
            Node nutrientAmountNode = graph.addNode("NutrientAmount");
            nutrientAmountNode.setProperty("nutrient_value", na.getValue());
            nutrientAmountNode.setProperty("standard_error", na.getStandardError());
            nutrientAmountNode.setProperty("number_of_observations", na.getNumberOfObservations());
            nutrientAmountNode.setProperty("date_of_entry", na.getDateOfEntry());
            graph.update(nutrientAmountNode);

            graph.addEdge(foodNodes.get(na.getFoodID()), nutrientAmountNode, "HAS_NUTRIENT");

            try {
                graph.addEdge(nutrientAmountNode, nutrientNodes.get(na.getNutrientID()), "OF_TYPE");
            } catch (GraphCacheException e) {
                LOGGER.warn("Nutrient with id " + na.getNutrientID() + " for food " + na.getFoodID() +
                            " not found. Ignoring.");
            }

            graph.addEdge(nutrientAmountNode, nutrientSourceNodes.get(na.getNutrientSourceID()), "COMES_FROM");
        }
    }

    /**
     * generates for every entry in the data files corresponding nodes in the graph.
     *
     * @param graph the graph given by the main program.
     */
    private void loadNodes(Graph graph) {
        for (Food f : dataSource.foods.values()) {
            Node foodNode = graph.addNode("Food");
            foodNode.setProperty("food_id", f.getId());
            foodNode.setProperty("food_code", f.getCode());
            foodNode.setProperty("description", f.getDescription());
            foodNode.setProperty("description_france", f.getDescriptionF());
            foodNode.setProperty("country_code", f.getCountryCode());
            foodNode.setProperty("date_of_entry", f.getDateOfEntry());
            foodNode.setProperty("date_of_publication", f.getDateOfPublication());
            foodNode.setProperty("scientific_name", f.getScientificName());
            graph.update(foodNode);
            foodNodes.put(f.getId(), foodNode);
        }

        for (FoodGroup fg : dataSource.foodGroups.values()) {
            Node foodGroupNode = graph.addNode("FoodGroup");
            foodGroupNode.setProperty("food_group_id", fg.getId());
            foodGroupNode.setProperty("food_group_code", fg.getCode());
            foodGroupNode.setProperty("name", fg.getName());
            foodGroupNode.setProperty("name_france", fg.getNameF());
            graph.update(foodGroupNode);
            foodGroupNodes.put(fg.getId(), foodGroupNode);
        }

        for (FoodSource fs : dataSource.foodSources.values()) {
            Node foodSourceNode = graph.addNode("FoodSource");
            foodSourceNode.setProperty("food_source_id", fs.getId());
            foodSourceNode.setProperty("food_source_code", fs.getCode());
            foodSourceNode.setProperty("description", fs.getDescription());
            foodSourceNode.setProperty("description_france", fs.getDescriptionF());
            graph.update(foodSourceNode);
            foodSourceNodes.put(fs.getId(), foodSourceNode);
        }

        for (Yield y : dataSource.yields.values()) {
            Node yieldNode = graph.addNode("Yield", "yield_id", y.getId(), "name", y.getName(), "name_france",
                                           y.getNameF());
            yieldNodes.put(y.getId(), yieldNode);
        }

        for (Refuse r : dataSource.refuses.values()) {
            Node refuseNode = graph.addNode("Refuse", "refuse_id", r.getId(), "name", r.getName(), "name_france",
                                            r.getNameF());
            refuseNodes.put(r.getId(), refuseNode);
        }

        for (Measure m : dataSource.measures.values()) {
            Node measureNode = graph.addNode("Measure", "measure_id", m.getId(), "name", m.getName(), "name_france",
                                             m.getNameF());
            measureNodes.put(m.getId(), measureNode);
        }

        for (NutrientSource ns : dataSource.nutrientSources.values()) {
            Node nutrientSourceNote = graph.addNode("NutrientSource");
            nutrientSourceNote.setProperty("nutrient_source_id", ns.getId());
            nutrientSourceNote.setProperty("nutrient_source_code", ns.getCode());
            nutrientSourceNote.setProperty("description", ns.getDescription());
            nutrientSourceNote.setProperty("description_france", ns.getDescriptionF());
            graph.update(nutrientSourceNote);
            nutrientSourceNodes.put(ns.getId(), nutrientSourceNote);
        }

        for (Nutrient n : dataSource.nutrients.values()) {
            Node nutrientNode = graph.addNode("Nutrient");
            nutrientNode.setProperty("nutrient_id", n.getId());
            nutrientNode.setProperty("nutrient_code", n.getCode());
            nutrientNode.setProperty("nutrient_symbol", n.getSymbol());
            nutrientNode.setProperty("unit", n.getUnit());
            nutrientNode.setProperty("name", n.getName());
            nutrientNode.setProperty("name_france", n.getNameF());
            nutrientNode.setProperty("infoods_tag",
                                     n.getTagName());    //sadly not every entry has one, so i can not use it as index
            nutrientNode.setProperty("decimals_of_value", n.getDecimals());
            graph.update(nutrientNode);
            nutrientNodes.put(n.getId(), nutrientNode);
        }
    }
}
