package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.CompoundNodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class ChEBIMappingDescriber extends MappingDescriber {
    public ChEBIMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (ChEBIGraphExporter.COMPOUND_LABEL.equals(localMappingLabel))
            return describeCompound(node);
        return null;
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        final var description = new CompoundNodeMappingDescription();
        description.addIdentifier(IdentifierType.CHEBI, node.<Integer>getProperty(GraphExporter.ID_KEY));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] parts = StringUtils.split(xref, "|", 3);
                switch (parts[1].toLowerCase()) {
                    case "cas":
                        description.addIdentifier(IdentifierType.CAS, parts[2]);
                        break;
                    case "drugbank":
                        description.addIdentifier(IdentifierType.DRUG_BANK, parts[2]);
                        break;
                    case "kegg.drug":
                    case "kegg.compound":
                        description.addIdentifier(IdentifierType.KEGG, parts[2]);
                        break;
                    case "drugcentral":
                        description.addIdentifier(IdentifierType.DRUG_CENTRAL, Integer.parseInt(parts[2]));
                        break;
                }
            }
        }
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                ChEBIGraphExporter.COMPOUND_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
