package de.unibi.agbi.biodwh2.iptmnet.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class IPTMNetMappingDescriber extends MappingDescriber {
    public IPTMNetMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (IPTMNetGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (IPTMNetGraphExporter.ORGANISM_LABEL.equals(localMappingLabel))
            return describeOrganism(node);
        return null;
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_id"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_accession"));
        description.addName(node.getProperty("gene_name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeOrganism(final Node node) {
        final var ncbiTaxId = node.<Integer>getProperty("ncbi_taxid");
        if (ncbiTaxId == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, ncbiTaxId);
        description.addNames(node.<String[]>getProperty("names"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{IPTMNetGraphExporter.PROTEIN_LABEL, IPTMNetGraphExporter.ORGANISM_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
