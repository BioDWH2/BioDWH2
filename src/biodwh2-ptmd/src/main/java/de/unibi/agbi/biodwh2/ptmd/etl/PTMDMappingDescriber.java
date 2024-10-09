package de.unibi.agbi.biodwh2.ptmd.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class PTMDMappingDescriber extends MappingDescriber {
    public PTMDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (PTMDGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProteinNode(node);
        if (PTMDGraphExporter.SPECIES_LABEL.equals(localMappingLabel))
            return describeSpeciesNode(node);
        // if (PTMDGraphExporter.PTM_LABEL.equals(localMappingLabel))
        //     return describePTMNode(node);
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

    private NodeMappingDescription[] describeSpeciesNode(final Node node) {
        final Integer ncbiTaxId = node.getProperty("ncbi_taxid");
        if (ncbiTaxId == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.NCBI_TAXON, ncbiTaxId);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    // private NodeMappingDescription[] describePTMNode(final Node node) {
    //     final String uniprotId = node.getProperty("uniprot_id");
    //     if (uniprotId == null)
    //         return null;
    //     final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PTM);
    //     description.addIdentifier(IdentifierType.UNIPROT_KB, uniprotId);
    //     return new NodeMappingDescription[]{description};
    // }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                PTMDGraphExporter.PROTEIN_LABEL, PTMDGraphExporter.SPECIES_LABEL, PTMDGraphExporter.PTM_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        // final var hasPTMPath = new PathMapping().add(PTMDGraphExporter.PROTEIN_LABEL, "HAS_PTM",
        //                                              PTMDGraphExporter.PTM_LABEL, EdgeDirection.FORWARD);
        // final var hasSpeciesPath = new PathMapping().add(PTMDGraphExporter.PTM_LABEL, "HAS_SPECIES",
        //                                                  PTMDGraphExporter.SPECIES_LABEL, EdgeDirection.FORWARD);
        // final var inDiseasePath = new PathMapping().add(PTMDGraphExporter.PTM_LABEL, "IN_DISEASE",
        //                                                 PTMDGraphExporter.DISEASE_LABEL, EdgeDirection.FORWARD);
        return new PathMapping[]{
                //hasPTMPath, hasSpeciesPath, inDiseasePath
        };
    }
}
