package de.unibi.agbi.biodwh2.hmdb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.PublicationNodeMappingDescription;

public class HMDBMappingDescriber extends MappingDescriber {
    public HMDBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HMDBGraphExporter.REFERENCE_LABEL.equals(localMappingLabel))
            return describeReferenceNode(node);
        if (HMDBGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDiseaseNode(node);
        if (HMDBGraphExporter.PATHWAY_LABEL.equals(localMappingLabel))
            return describePathwayNode(node);
        if (HMDBGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProteinNode(node);
        if (HMDBGraphExporter.METABOLITE_LABEL.equals(localMappingLabel))
            return describeMetaboliteNode(node);
        return null;
    }

    private NodeMappingDescription[] describeReferenceNode(final Node node) {
        final Integer pubmedId = node.getProperty("pubmed_id");
        if (pubmedId == null)
            return null;
        final PublicationNodeMappingDescription description = new PublicationNodeMappingDescription();
        description.pubmedId = pubmedId;
        description.addIdentifier(IdentifierType.PUBMED_ID, pubmedId);
        description.addName(node.getProperty("text"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDiseaseNode(final Node node) {
        final Integer omimId = node.getProperty("omim_id");
        if (omimId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addIdentifier(IdentifierType.OMIM, omimId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePathwayNode(final Node node) {
        final String keggMapId = node.getProperty("kegg_map_id");
        final String smpdbId = node.getProperty("smpdb_id");
        if (keggMapId == null && smpdbId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        if (keggMapId != null)
            description.addIdentifier(IdentifierType.KEGG, keggMapId);
        if (smpdbId != null)
            description.addIdentifier(IdentifierType.SMPDB, smpdbId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProteinNode(final Node node) {
        final String accession = node.getProperty("accession");
        final String uniprotId = node.getProperty("uniprot_id");
        if (accession == null && uniprotId == null)
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        // if (accession != null)
        //     description.addIdentifier("HMDB", accession);
        if (uniprotId != null)
            description.addIdentifier(IdentifierType.UNIPROT_KB, uniprotId);
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("uniprot_name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMetaboliteNode(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.METABOLITE);
        // if (accession != null)
        //     description.addIdentifier("HMDB", accession);
        description.addName(node.getProperty("name"));
        return null;
        // TODO: return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                HMDBGraphExporter.REFERENCE_LABEL, HMDBGraphExporter.DISEASE_LABEL, HMDBGraphExporter.PATHWAY_LABEL,
                HMDBGraphExporter.PROTEIN_LABEL, HMDBGraphExporter.METABOLITE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
