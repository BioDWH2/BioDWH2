package de.unibi.agbi.biodwh2.brenda.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class BrendaMappingDescriber extends MappingDescriber {
    public BrendaMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (BrendaGraphExporter.ENZYME_LABEL.equals(localMappingLabel))
            return describeEnzyme(node);
        if (BrendaGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (BrendaGraphExporter.PUBLICATION_LABEL.equals(localMappingLabel))
            return describePublication(node);
        if (BrendaGraphExporter.ORGANISM_LABEL.equals(localMappingLabel))
            return describeOrganism(node);
        return null;
    }

    private NodeMappingDescription[] describeEnzyme(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.EC_NUMBER, node.<String>getProperty(GraphExporter.ID_KEY));
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("systematic_name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final String accession = node.getProperty("accession");
        if (accession == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, accession);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePublication(final Node node) {
        final Integer pubmedId = node.getProperty("pmid");
        if (pubmedId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PUBLICATION);
        description.addIdentifier(IdentifierType.PUBMED_ID, pubmedId);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeOrganism(final Node node) {
        final Integer taxonomyId = node.getProperty("ncbi_taxid");
        if (taxonomyId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, taxonomyId);
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
                BrendaGraphExporter.ENZYME_LABEL, BrendaGraphExporter.PROTEIN_LABEL,
                BrendaGraphExporter.PUBLICATION_LABEL, BrendaGraphExporter.ORGANISM_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return null;
    }
}
