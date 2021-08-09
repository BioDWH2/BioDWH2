package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

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
        if (KeggGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (KeggGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        final String[] names = getEntryNames(node);
        description.addNames(names);
        for (final String name : names) {
            if (name.endsWith(")")) {
                final int startIndex = name.lastIndexOf('(');
                final String nameWithoutTags = name.substring(0, startIndex).trim();
                final String[] nameTags = StringUtils.split(name.substring(startIndex + 1, name.length() - 1), '/');
                for (final String nameTag : nameTags)
                    if (nameTag.equals("INN")) {
                        description.addIdentifier(IdentifierType.INTERNATIONAL_NONPROPRIETARY_NAMES, nameWithoutTags);
                        break;
                    }
            }
        }
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

    private String[] getEntryNames(final Node node) {
        final String name = node.getProperty("name");
        if (name != null)
            return new String[]{name.trim()};
        final String[] names = node.getProperty("names");
        return names != null ? Arrays.stream(names).map(String::trim).toArray(String[]::new) : new String[0];
    }

    private NodeMappingDescription[] describeReference(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.DOI, node.<String>getProperty("doi"));
        description.addIdentifier(IdentifierType.PUBMED_ID, node.<Integer>getProperty("pmid"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addNames(node.<String>getProperty("name"));
        description.addNames(node.<String[]>getProperty("symbols"));
        description.addIdentifier(IdentifierType.KEGG, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addNames(getEntryNames(node));
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
                KeggGraphExporter.DRUG_LABEL, KeggGraphExporter.REFERENCE_LABEL, KeggGraphExporter.GENE_LABEL,
                KeggGraphExporter.DISEASE_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1 && edges[0].getLabel().endsWith(KeggGraphExporter.TARGETS_LABEL))
            return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(KeggGraphExporter.DRUG_LABEL, KeggGraphExporter.TARGETS_LABEL,
                                      KeggGraphExporter.GENE_LABEL, EdgeDirection.FORWARD)
        };
    }
}
