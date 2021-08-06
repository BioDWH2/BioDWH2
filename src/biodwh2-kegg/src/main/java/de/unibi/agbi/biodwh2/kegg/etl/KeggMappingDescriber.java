package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class KeggMappingDescriber extends MappingDescriber {
    public KeggMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (KeggGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (KeggGraphExporter.REFERENCE_LABEL.equals(localMappingLabel))
            return describeReference(node);
        if (KeggGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addNames(node.<String>getProperty("name"));
        description.addNames(node.<String[]>getProperty("names"));
        final String[] externalIdentifier = node.getProperty("external_identifier");
        if (externalIdentifier != null)
            for (final String identifier : externalIdentifier) {
                final String[] idParts = StringUtils.split(identifier, ":", 2);
                if ("DrugBank".equals(idParts[0]))
                    description.addIdentifier(IdentifierType.DRUG_BANK, idParts[1]);
                else if ("CAS".equals(idParts[0]))
                    description.addIdentifier(IdentifierType.CAS, idParts[1]);
            }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeReference(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.DOI, node.<String>getProperty("doi"));
        description.addIdentifier(IdentifierType.PUBMED_ID, node.<Integer>getProperty("pmid"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addNames(node.<String>getProperty("name"));
        description.addNames(node.<String[]>getProperty("names"));
        final String[] externalIdentifier = node.getProperty("external_identifier");
        if (externalIdentifier != null)
            for (final String identifier : externalIdentifier) {
                final String[] idParts = StringUtils.split(identifier, ":", 2);
                if ("ICD-10".equals(idParts[0]))
                    description.addIdentifier(IdentifierType.ICD10, idParts[1]);
                else if ("ICD-11".equals(idParts[0]))
                    description.addIdentifier(IdentifierType.ICD11, idParts[1]);
                else if ("OMIM".equals(idParts[0]))
                    description.addIdentifier(IdentifierType.OMIM, idParts[1]);
            }
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                KeggGraphExporter.DRUG_LABEL, KeggGraphExporter.REFERENCE_LABEL, KeggGraphExporter.DISEASE_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
