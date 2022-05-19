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
        if ("Plant".equals(localMappingLabel))
            return describePlant(node);
        if ("Ingredient".equals(localMappingLabel))
            return describeIngredient(node);
        if ("Target".equals(localMappingLabel))
            return describeTarget(node);
        return null;
    }

    private NodeMappingDescription[] describePlant(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("species_name"));
        final String speciesTaxId = node.getProperty("species_tax_id");
        if (speciesTaxId != null && speciesTaxId.length() > 0)
            description.addIdentifier(IdentifierType.NCBI_TAXON, Integer.parseInt(speciesTaxId));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeIngredient(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("pref_name"));
        description.addName(node.getProperty("iupac_name"));
        description.addIdentifier(IdentifierType.CHEMBL, node.<String>getProperty("chembl_id"));
        final String pubchemCid = node.getProperty("pubchem_cid");
        if (pubchemCid != null && pubchemCid.length() > 0) {
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
        // TODO: ttd_id
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Plant", "Ingredient", "Target"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
