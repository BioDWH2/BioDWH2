package de.unibi.agbi.biodwh2.negatome.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class NegatomeMappingDescriber extends MappingDescriber {
    public NegatomeMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (NegatomeGraphExporter.PROTEIN_LABEL.equals(localMappingLabel)) {
            final NodeMappingDescription description = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.PROTEIN);
            description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("id"));
            return new NodeMappingDescription[]{description};
        }
        if (NegatomeGraphExporter.PFAM_DOMAIN_LABEL.equals(localMappingLabel)) {
            final NodeMappingDescription description = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.PROTEIN_DOMAIN);
            description.addIdentifier(IdentifierType.PFAM, node.<String>getProperty("id"));
            return new NodeMappingDescription[]{description};
        }
        return new NodeMappingDescription[0];
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{NegatomeGraphExporter.PROTEIN_LABEL, NegatomeGraphExporter.PFAM_DOMAIN_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
