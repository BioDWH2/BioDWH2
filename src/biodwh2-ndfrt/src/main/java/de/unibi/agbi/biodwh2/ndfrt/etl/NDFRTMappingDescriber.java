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
    public NodeMappingDescription describe(final Graph graph, final Node node) {
        if (node.getLabel().endsWith("DRUG"))
            return describeDrug(node);
        if (node.getLabel().endsWith("INGREDIENT"))
            return describeIngredient(node);
        if (node.getLabel().endsWith("DISEASE"))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        addIdentifierIfNotEmpty(description, node, IdentifierType.NDF_RT_NUI, "NUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.RX_NORM_CUI, "RxNorm_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UMLS_CUI, "UMLS_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.VANDF_VUID, "VUID");
        return description;
    }

    private void addIdentifierIfNotEmpty(final NodeMappingDescription description, final Node node,
                                         final IdentifierType type, final String propertyKey) {
        final String identifier = node.getProperty(propertyKey);
        if (StringUtils.isNotEmpty(identifier))
            description.addIdentifier(type, identifier);
    }

    private NodeMappingDescription describeIngredient(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        addIdentifierIfNotEmpty(description, node, IdentifierType.NDF_RT_NUI, "NUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.RX_NORM_CUI, "RxNorm_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UMLS_CUI, "UMLS_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.MESH, "MeSH_DUI");
        return description;
    }

    private NodeMappingDescription describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        addIdentifierIfNotEmpty(description, node, IdentifierType.NDF_RT_NUI, "NUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.RX_NORM_CUI, "RxNorm_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.UMLS_CUI, "UMLS_CUI");
        addIdentifierIfNotEmpty(description, node, IdentifierType.SNOMED_CT, "SNOMED_CID");
        addIdentifierIfNotEmpty(description, node, IdentifierType.MESH, "MeSH_DUI");
        return description;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"DRUG", "INGREDIENT", "DISEASE"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
