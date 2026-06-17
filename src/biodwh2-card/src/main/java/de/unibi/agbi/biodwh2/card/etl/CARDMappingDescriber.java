package de.unibi.agbi.biodwh2.card.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;
import de.unibi.agbi.biodwh2.core.model.graph.PathMapping;
import de.unibi.agbi.biodwh2.core.model.graph.PathMappingDescription;
import org.apache.commons.lang3.StringUtils;

public final class CARDMappingDescriber extends MappingDescriber {
    private static final String PROTEIN_LABEL = "Protein";
    private static final String DNA_SEQUENCE_LABEL = "DNASequence";
    private static final String TAXON_LABEL = "Taxon";

    public CARDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (DNA_SEQUENCE_LABEL.equals(localMappingLabel))
            return describeDnaSequence(node);
        if (TAXON_LABEL.equals(localMappingLabel))
            return describeTaxon(node);
        return null;
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final String accession = node.getProperty("accession");
        if (StringUtils.isBlank(accession))
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.GENBANK, stripVersion(accession));
        description.addName(accession);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDnaSequence(final Node node) {
        final String accession = node.getProperty("accession");
        if (StringUtils.isBlank(accession))
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.GENBANK, stripVersion(accession));
        description.addName(accession);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTaxon(final Node node) {
        final Integer ncbiTaxId = node.getProperty("ncbi_taxid");
        if (ncbiTaxId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, ncbiTaxId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    /**
     * Removes a trailing version suffix from a GenBank accession (e.g. "ACT97415.1" -> "ACT97415") so it matches the
     * GenBank identifier pattern.
     */
    private String stripVersion(final String accession) {
        final int dotIndex = accession.indexOf('.');
        return dotIndex < 0 ? accession : accession.substring(0, dotIndex);
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{PROTEIN_LABEL, DNA_SEQUENCE_LABEL, TAXON_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}