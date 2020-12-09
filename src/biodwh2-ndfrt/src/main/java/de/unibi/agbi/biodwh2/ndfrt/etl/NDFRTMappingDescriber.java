package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class NDFRTMappingDescriber extends MappingDescriber {
    public NDFRTMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Drug".equals(localMappingLabel))
            return describeDrug(node);
        if ("Ingredient".equals(localMappingLabel))
            return describeIngredient(node);
        if ("Disease".equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        addIdentifierIfNotEmpty(description, node, IdentifierType.NDF_RT_NUI, "NUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.RX_NORM_CUI, "RxNorm_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UMLS_CUI, "UMLS_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.VANDF_VUID, "VUID");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UNII, "FDA_UNII");
        return new NodeMappingDescription[]{description};
    }

    private void addIdentifierIfNotEmpty(final NodeMappingDescription description, final Node node,
                                         final IdentifierType type, final String propertyKey) {
        final String identifier = node.getProperty(propertyKey);
        if (StringUtils.isNotEmpty(identifier))
            description.addIdentifier(type, identifier);
    }

    private NodeMappingDescription[] describeIngredient(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        addIdentifierIfNotEmpty(description, node, IdentifierType.NDF_RT_NUI, "NUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.RX_NORM_CUI, "RxNorm_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UMLS_CUI, "UMLS_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.MESH, "MeSH_DUI");
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        addIdentifierIfNotEmpty(description, node, IdentifierType.NDF_RT_NUI, "NUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.RX_NORM_CUI, "RxNorm_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UMLS_CUI, "UMLS_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.SNOMED_CT, "SNOMED_CID");
        addIdentifierIfNotEmpty(description, node, IdentifierType.MESH, "MeSH_DUI");
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Ingredient", "Disease"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges[0].getLabel().endsWith("INDUCES"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INDUCES);
        if (edges[0].getLabel().endsWith("CI_WITH") || edges[0].getLabel().endsWith("CI_CHEMCLASS"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.CONTRAINDICATES);
        if (edges[0].getLabel().endsWith("MAY_TREAT"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INDICATES);
        if (edges[0].getLabel().endsWith("EFFECT_MAY_BE_INHIBITED_BY"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INTERACTS);
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[][]{
                {"Drug", "INDUCES", "Disease"}, {"Drug", "CI_WITH", "Disease"}, {"Drug", "MAY_TREAT", "Disease"},
                {"Drug", "EFFECT_MAY_BE_INHIBITED_BY", "Drug"}, {"Drug", "CI_CHEMCLASS", "Ingredient"}
        };
    }
}
