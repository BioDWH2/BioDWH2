package de.unibi.agbi.biodwh2.rnadisease.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;

public class RNADiseaseMappingDescriber extends MappingDescriber {
    public RNADiseaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (RNADiseaseGraphExporter.RNA_LABEL.equals(localMappingLabel))
            return describeRNA(node);
        if (RNADiseaseGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeRNA(Node node) {
        final String type = node.getProperty("type");
        if (type != null) {
            switch (type) {
                case "lncRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.LNC_RNA);
                case "miRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.MI_RNA);
                case "piRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.PI_RNA);
                case "mRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.M_RNA);
                case "rRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.R_RNA);
                case "scRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.SC_RNA);
                case "snoRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.SNO_RNA);
                case "snRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.SN_RNA);
                case "tRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.T_RNA);
                case "YRNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.Y_RNA);
                case "antisense non-coding RNA":
                    return populateRNANode(node, RNANodeMappingDescription.RNAType.ANTISENSE_RNA);
                case "siRNA":
                case "mascRNA":
                case "mtRNA":
                case "other":
                case "pseudo":
                case "unknown":
                case "circRNA":
                    // TODO
                    break;
            }
        }
        return null;
    }

    private NodeMappingDescription[] populateRNANode(final Node node, final RNANodeMappingDescription.RNAType rnaType) {
        final RNANodeMappingDescription description = new RNANodeMappingDescription(rnaType);
        // TODO: description.addIdentifier();
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        // TODO
        return null;
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{RNADiseaseGraphExporter.RNA_LABEL, RNADiseaseGraphExporter.DISEASE_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
