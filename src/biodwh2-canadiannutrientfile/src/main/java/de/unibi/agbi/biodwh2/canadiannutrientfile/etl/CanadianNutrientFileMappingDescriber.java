package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class CanadianNutrientFileMappingDescriber extends MappingDescriber {
    public CanadianNutrientFileMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (CanadianNutrientFileGraphExporter.FOOD_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeFood(node);
        if (CanadianNutrientFileGraphExporter.FOOD_GROUP_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeFoodGroup(node);
        if (CanadianNutrientFileGraphExporter.NUTRIENT_LABEL.equalsIgnoreCase(localMappingLabel))
            return describeNutrient(node);
        return null;
    }

    private NodeMappingDescription[] describeFood(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription("FOOD");
        description.addName(node.getProperty("scientific_name"));
        description.addName(node.getProperty("description"));
        description.addName(node.getProperty("description_french"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeFoodGroup(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription("FOOD_GROUP");
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_french"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeNutrient(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription("NUTRIENT");
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("name_french"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                CanadianNutrientFileGraphExporter.FOOD_LABEL, CanadianNutrientFileGraphExporter.FOOD_GROUP_LABEL,
                CanadianNutrientFileGraphExporter.NUTRIENT_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
