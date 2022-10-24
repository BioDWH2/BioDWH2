package de.unibi.agbi.biodwh2.clinicaltrialsgov.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class ClinicalTrialsGovMappingDescriber extends MappingDescriber {
    public ClinicalTrialsGovMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Trial".equals(localMappingLabel)) {
            final NodeMappingDescription description = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.CLINICAL_TRIAL);
            description.addIdentifier(IdentifierType.NCT_NUMBER, node.<String>getProperty("id"));
            return new NodeMappingDescription[]{description};
        }
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Trial"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
