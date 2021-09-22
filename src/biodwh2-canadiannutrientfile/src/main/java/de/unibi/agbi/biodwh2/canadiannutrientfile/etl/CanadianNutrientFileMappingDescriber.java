package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class CanadianNutrientFileMappingDescriber extends MappingDescriber {
    public CanadianNutrientFileMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(Graph graph, Node node, String localMappingLabel) {
        if ("Food".equalsIgnoreCase(localMappingLabel))
            return describeFood(node);
        if ("FoodGroup".equalsIgnoreCase(localMappingLabel))
            return describeFoodGroup(node);
        if ("Yield".equalsIgnoreCase(localMappingLabel))
            return describeYield(node);
        if ("Refuse".equalsIgnoreCase(localMappingLabel))
            return describeRefuse(node);
        if ("Measure".equalsIgnoreCase(localMappingLabel))
            return describeMeasure(node);
        if ("Nutrient".equalsIgnoreCase(localMappingLabel))
            return describeNutrient(node);
        return null;
    }

    private NodeMappingDescription[] describeNutrient(Node node) {
        NodeMappingDescription description = new NodeMappingDescription("NUTRIENT");
        if (node.<String>getProperty("name").length() == 0 &&
            node.<String>getProperty("name_france").length() == 0){
            return null;
        }
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_france"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMeasure(Node node) {
        NodeMappingDescription description = new NodeMappingDescription("MEASURE");
        if (node.<String>getProperty("name").length() == 0 &&
            node.<String>getProperty("name_france").length() == 0){
            return null;
        }
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_france"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeRefuse(Node node) {
        NodeMappingDescription description = new NodeMappingDescription("REFUSE");
        if (node.<String>getProperty("name").length() == 0 &&
            node.<String>getProperty("name_france").length() == 0){
            return null;
        }
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_france"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeYield(Node node) {
        NodeMappingDescription description = new NodeMappingDescription("YIELD");
        if (node.<String>getProperty("name").length() == 0 &&
            node.<String>getProperty("name_france").length() == 0){
            return null;
        }
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_france"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeFoodGroup(Node node) {
        NodeMappingDescription description = new NodeMappingDescription("FOODGROUP");
        if (node.<String>getProperty("name").length() == 0 &&
            node.<String>getProperty("name_france").length() == 0){
            return null;
        }
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_france"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeFood(Node node) {
        NodeMappingDescription description = new NodeMappingDescription("FOOD");
        if (node.<String>getProperty("scientific_name").length() != 0){
            description.addName(node.getProperty("scientific_name"));
            return new NodeMappingDescription[]{description};
        }
        return null;
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Food", "FoodGroup", "Yield", "Refuse", "Measure", "Nutrient"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
