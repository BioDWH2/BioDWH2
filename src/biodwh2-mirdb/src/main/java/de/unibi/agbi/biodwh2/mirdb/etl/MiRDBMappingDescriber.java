package de.unibi.agbi.biodwh2.mirdb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class MiRDBMappingDescriber extends MappingDescriber {
    public MiRDBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (MiRDBGraphExporter.M_RNA_LABEL.equals(localMappingLabel))
            return describeMRNA(node);
        if (MiRDBGraphExporter.MI_RNA_LABEL.equals(localMappingLabel))
            return describeMiRNA(node);
        return null;
    }

    private NodeMappingDescription[] describeMRNA(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
        description.addIdentifier(IdentifierType.GENBANK,
                                  node.<String>getProperty(MiRDBGraphExporter.GENBANK_ACCESSION_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMiRNA(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
        description.addIdentifier(IdentifierType.MIRNA, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{MiRDBGraphExporter.M_RNA_LABEL, MiRDBGraphExporter.MI_RNA_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
