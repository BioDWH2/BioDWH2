package de.unibi.agbi.biodwh2.adrecs.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class ADReCSMappingDescriber extends MappingDescriber {
    public ADReCSMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (ADReCSGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (ADReCSGraphExporter.ADR_LABEL.equals(localMappingLabel))
            return describeADR(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.<String>getProperty("pubchem_cid"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeADR(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.ADVERSE_EVENT);
        description.addName(node.getProperty("term"));
        description.addIdentifier(IdentifierType.MEDDRA, node.<String>getProperty("meddra_code"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{ADReCSGraphExporter.DRUG_LABEL, ADReCSGraphExporter.ADR_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
