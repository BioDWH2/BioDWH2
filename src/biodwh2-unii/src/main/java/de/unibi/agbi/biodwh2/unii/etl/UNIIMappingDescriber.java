package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.util.Set;

public class UNIIMappingDescriber extends MappingDescriber {
    public UNIIMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("UNII".equals(localMappingLabel))
            return describeCompound(node);
        if (UNIIGraphExporter.SPECIES_LABEL.equals(localMappingLabel))
            return describeSpecies(node);
        return null;
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addName(node.getProperty("preferred_term"));
        description.addNames(node.<String[]>getProperty("official_names"));
        description.addIdentifier(IdentifierType.UNII, node.<String>getProperty("id"));
        description.addIdentifier(IdentifierType.CAS, node.<String>getProperty("cas"));
        final Integer pubchemCid = tryParseInt(node.getProperty("pubchem_cid"));
        if (pubchemCid != null)
            description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, pubchemCid);
        description.addIdentifier(IdentifierType.EUROPEAN_CHEMICALS_AGENCY_EC, node.<String>getProperty("ec"));
        description.addIdentifier(IdentifierType.RX_NORM_CUI, node.<String>getProperty("rx_cui"));
        description.addIdentifier(IdentifierType.INN, node.<String>getProperty("inn_id"));
        return new NodeMappingDescription[]{description};
    }

    public Integer tryParseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private NodeMappingDescription[] describeSpecies(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.ITIS, node.<Set<Integer>>getProperty(UNIIGraphExporter.ITIS_IDS_KEY));
        description.addIdentifier(IdentifierType.NCBI_TAXON,
                                  node.<Set<Integer>>getProperty(UNIIGraphExporter.NCBI_TAXONOMY_IDS_KEY));
        description.addIdentifier(IdentifierType.USDA_PLANTS_SYMBOL,
                                  node.<Set<String>>getProperty(UNIIGraphExporter.USDA_PLANTS_SYMBOLS_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{UNIIGraphExporter.UNII_LABEL, UNIIGraphExporter.SPECIES_LABEL};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
