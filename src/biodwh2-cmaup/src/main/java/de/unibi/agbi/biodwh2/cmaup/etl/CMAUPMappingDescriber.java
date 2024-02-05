package de.unibi.agbi.biodwh2.cmaup.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class CMAUPMappingDescriber extends MappingDescriber {
    public CMAUPMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (CMAUPGraphExporter.PLANT_LABEL.equals(localMappingLabel))
            return describePlant(node);
        if (CMAUPGraphExporter.INGREDIENT_LABEL.equals(localMappingLabel))
            return describeIngredient(node);
        if (CMAUPGraphExporter.TARGET_LABEL.equals(localMappingLabel))
            return describeTarget(node);
        return null;
    }

    private NodeMappingDescription[] describePlant(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("species_name"));
        final Integer speciesTaxId = node.getProperty("species_tax_id");
        if (speciesTaxId != null)
            description.addIdentifier(IdentifierType.NCBI_TAXON, speciesTaxId);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeIngredient(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("pref_name"));
        description.addName(node.getProperty("iupac_name"));
        description.addIdentifier(IdentifierType.CHEMBL, node.<String>getProperty("chembl_id"));
        final String pubchemCid = node.getProperty("pubchem_cid");
        if (pubchemCid != null && !pubchemCid.isEmpty()) {
            final String[] parts = StringUtils.split(pubchemCid, ';');
            for (final String part : parts)
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(part));
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeTarget(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("protein_name"));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("gene_symbol"));
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_id"));
        description.addIdentifier(IdentifierType.CHEMBL, node.<String>getProperty("chembl_id"));
        // ttd_id
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1)
            return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                CMAUPGraphExporter.PLANT_LABEL, CMAUPGraphExporter.INGREDIENT_LABEL, CMAUPGraphExporter.TARGET_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        final PathMapping targetsPath = new PathMapping().add(CMAUPGraphExporter.INGREDIENT_LABEL,
                                                              CMAUPGraphExporter.TARGETS_LABEL,
                                                              CMAUPGraphExporter.TARGET_LABEL);
        return new PathMapping[]{targetsPath};
    }
}
