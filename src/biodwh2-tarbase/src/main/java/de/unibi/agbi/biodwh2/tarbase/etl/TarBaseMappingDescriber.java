package de.unibi.agbi.biodwh2.tarbase.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class TarBaseMappingDescriber extends MappingDescriber {
    private final static String[] ENSEMBL_GENE_ID_PREFIXES = new String[]{
            "ENSDARG", "ENSG", "ENSGALG", "ENSMMUG", "ENSMUSG", "ENSPTRG", "ENSRNOG"
    };

    public TarBaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Gene".equals(localMappingLabel)) {
            final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
            final String id = node.getProperty("id");
            description.addName(node.getProperty("name"));
            if (id != null) {
                if (id.startsWith("WBGene")) {
                    description.addIdentifier(IdentifierType.WORM_BASE, id);
                } else if (id.startsWith("NM_")) {
                    // TODO description.addIdentifier(IdentifierType., id);
                } else {
                    for (final String ensemblGeneIdPrefix : ENSEMBL_GENE_ID_PREFIXES) {
                        if (id.startsWith(ensemblGeneIdPrefix)) {
                            description.addIdentifier(IdentifierType.ENSEMBL_GENE_ID, id);
                        }
                    }
                }
            }
            return new NodeMappingDescription[]{description};
        }
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Gene"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
