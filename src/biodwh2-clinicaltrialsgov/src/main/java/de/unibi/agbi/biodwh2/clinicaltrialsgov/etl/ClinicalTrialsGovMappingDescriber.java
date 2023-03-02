package de.unibi.agbi.biodwh2.clinicaltrialsgov.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;

public class ClinicalTrialsGovMappingDescriber extends MappingDescriber {
    public ClinicalTrialsGovMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Trial".equals(localMappingLabel))
            return describeTrial(node);
        if ("Reference".equals(localMappingLabel))
            return describeReference(node);
        return null;
    }

    private static NodeMappingDescription[] describeTrial(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.CLINICAL_TRIAL);
        description.addIdentifier(IdentifierType.NCT_NUMBER, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private static NodeMappingDescription[] describeReference(final Node node) {
        final PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
        description.pubmedId = node.<Integer>getProperty("pmid");
        description.addIdentifier(IdentifierType.PUBMED_ID, description.pubmedId);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Trial", "Reference"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
