package de.unibi.agbi.biodwh2.rnalocate.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class RNALocateMappingDescriber extends MappingDescriber {
    public RNALocateMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (RNALocateGraphExporter.RNA_LABEL.equals(localMappingLabel))
            return describeRNANode(node);
        return null;
    }

    private NodeMappingDescription[] describeRNANode(final Node node) {
        final String category = node.getProperty("category");
        if (category == null)
            return null;
        switch (category) {
            case "piRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.PI_RNA);
            case "miRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.MI_RNA);
            case "lncRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.LNC_RNA);
            case "antisense RNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.ANTISENSE_RNA);
            case "snoRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.SNO_RNA);
            case "snRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.SN_RNA);
            case "tRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.T_RNA);
            case "rRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.R_RNA);
            case "mRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.M_RNA);
            case "Y RNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.Y_RNA);
            case "scRNA":
                return describeRNANode(node, RNANodeMappingDescription.RNAType.SC_RNA);
            // TODO
            case "lincRNA":
            case "ncRNA":
            case "processed_transcript":
            case "pseudo":
            case "circRNA":
            case "csRNA":
            case "other":
            case "miscRNA":
            case "unknown":
            case "QTL":
            case "DNA segment":
            case "unclassified RNA":
            case "cnRNA":
            case "scaRNA":
            case "mtRNA":
            case "cRNA":
            case "vRNA":
            default:
                return null;
        }
    }

    private NodeMappingDescription[] describeRNANode(final Node node, final RNANodeMappingDescription.RNAType type) {
        final NodeMappingDescription description = new RNANodeMappingDescription(type);
        final String id = node.getProperty("id");
        if (id != null) {
            final String[] idParts = StringUtils.split(id, ":", 2);
            switch (idParts[0]) {
                case "miRBase":
                    description.addIdentifier(IdentifierType.MIRBASE, idParts[1]);
                    break;
                case "NCBI":
                    description.addIdentifier(IdentifierType.NCBI_GENE, idParts[1]);
                    break;
                case "exoRBase":
                    break;
                case "circBase":
                    break;
                case "RNAcentral":
                    break;
                case "MGI":
                    break;
            }
        }
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{RNALocateGraphExporter.RNA_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
