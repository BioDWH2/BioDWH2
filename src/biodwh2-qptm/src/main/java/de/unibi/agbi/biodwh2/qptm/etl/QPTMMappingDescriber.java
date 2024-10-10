package de.unibi.agbi.biodwh2.qptm.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class QPTMMappingDescriber extends MappingDescriber {
    public QPTMMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (QPTMGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProteinNode(node);
        if (QPTMGraphExporter.ORGANISM_LABEL.equals(localMappingLabel))
            return describeOrganismNode(node);
        return null;
    }

    private NodeMappingDescription[] describeProteinNode(final Node node) {
        final String uniprotId = node.getProperty("uniprot_id");
        if (uniprotId == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, uniprotId);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeOrganismNode(final Node node) {
        final Integer ncbiTaxId = node.getProperty("ncbi_taxid");
        if (ncbiTaxId == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, ncbiTaxId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{QPTMGraphExporter.PROTEIN_LABEL, QPTMGraphExporter.ORGANISM_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
