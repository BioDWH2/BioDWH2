package de.unibi.agbi.biodwh2.tarbase.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;

public class TarBaseMappingDescriber extends MappingDescriber {
    private static final String ENSEMBL_HUMAN_GENE_ID_PREFIX = "ENSG";
    private static final String[] ENSEMBL_GENE_ID_PREFIXES = new String[]{
            "ENSDARG", "ENSGALG", "ENSMMUG", "ENSMUSG", "ENSPTRG", "ENSRNOG", ENSEMBL_HUMAN_GENE_ID_PREFIX
    };

    public TarBaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (TarBaseGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (TarBaseGraphExporter.MIRNA_LABEL.equals(localMappingLabel))
            return describeRNA(node);
        return null;
    }

    private static NodeMappingDescription[] describeGene(final Node node) {
        final String id = node.getProperty("id");
        if (id == null)
            return null;
        // If we have an actual mRNA identifier from GenBank, we map it as such
        if (id.startsWith("NM_") || id.startsWith("XM_")) {
            final NodeMappingDescription description = new RNANodeMappingDescription(
                    RNANodeMappingDescription.RNAType.M_RNA);
            description.addName(node.getProperty("name"));
            description.addIdentifier(IdentifierType.GENBANK, id);
            return new NodeMappingDescription[]{description};
        }
        // Otherwise we have to go with a Gene
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        if (id.startsWith("WBGene")) {
            description.addIdentifier(IdentifierType.WORM_BASE, id);
        } else {
            final String species = node.getProperty("species");
            for (final String ensemblGeneIdPrefix : ENSEMBL_GENE_ID_PREFIXES) {
                if (id.startsWith(ensemblGeneIdPrefix)) {
                    // Check if species matches prefix
                    if (!ENSEMBL_HUMAN_GENE_ID_PREFIX.equals(ensemblGeneIdPrefix) ||
                        SpeciesLookup.HOMO_SAPIENS.scientificName.equals(species))
                        description.addIdentifier(IdentifierType.ENSEMBL, id);
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private static NodeMappingDescription[] describeRNA(final Node node) {
        final NodeMappingDescription description = new RNANodeMappingDescription(
                RNANodeMappingDescription.RNAType.MI_RNA);
        final String id = node.getProperty("id");
        description.addIdentifier(IdentifierType.MIRNA, id);
        description.addName(id);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{TarBaseGraphExporter.GENE_LABEL, TarBaseGraphExporter.MIRNA_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
