package de.unibi.agbi.biodwh2.aact.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class AACTMappingDescriber extends MappingDescriber {
    public AACTMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (AACTGraphExporter.STUDY_LABEL.equals(localMappingLabel))
            return describeStudy(node);
        if (AACTGraphExporter.REFERENCE_LABEL.equals(localMappingLabel))
            return describeReference(node);
        return null;
    }

    public NodeMappingDescription[] describeStudy(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.CLINICAL_TRIAL);
        description.addIdentifier(IdentifierType.NCT_NUMBER, node.<String>getProperty("nct_id"));
        return new NodeMappingDescription[]{description};
    }

    public NodeMappingDescription[] describeReference(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        final String citation = node.getProperty("citation");
        if (citation != null) {
            final String[] dois = IdentifierUtils.extractDois(citation);
            if (dois != null)
                for (final String doi : dois)
                    description.addIdentifier(IdentifierType.DOI, doi);
            description.addName(citation);
        }
        final Integer pmid = node.getProperty("pmid");
        if (pmid != null)
            description.addIdentifier(IdentifierType.PUBMED_ID, pmid);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                AACTGraphExporter.STUDY_LABEL, AACTGraphExporter.REFERENCE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
