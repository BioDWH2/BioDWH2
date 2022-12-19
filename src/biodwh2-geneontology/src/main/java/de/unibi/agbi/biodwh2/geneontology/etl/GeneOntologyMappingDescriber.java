package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class GeneOntologyMappingDescriber extends MappingDescriber {
    public GeneOntologyMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (GeneOntologyGraphExporter.DB_OBJECT_LABEL.equals(localMappingLabel))
            return describeDBObject(node);
        return null;
    }

    private NodeMappingDescription[] describeDBObject(final Node node) {
        final String type = node.getProperty("type");
        if (type != null) {
            // gene_product is not further specified and a fallback, therefore a precise mapping is difficult
            switch (type) {
                case "protein":
                case "protein_complex":
                    return populateDescription(node,
                                               new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN));
                case "miRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.MI_RNA);
                case "lnc_RNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.LNC_RNA);
                case "rRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.R_RNA);
                case "tRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.T_RNA);
                case "scRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.SC_RNA);
                case "snoRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.SNO_RNA);
                case "snRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.SN_RNA);
                case "piRNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.PI_RNA);
                case "hammerhead_ribozyme":
                case "ribozyme":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.RIBOZYME);
                case "antisense_RNA":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.ANTISENSE_RNA);
                case "ncRNA":
                case "guide_RNA":
                case "RNase_MRP_RNA":
                case "RNase_P_RNA":
                case "SRP_RNA":
                case "telomerase_RNA":
                case "primary_transcript":
                case "transcript":
                    return createRNAMappingDescription(node, RNANodeMappingDescription.RNAType.UNKNOWN);
            }
        }
        return null;
    }

    private NodeMappingDescription[] populateDescription(final Node node, final NodeMappingDescription description) {
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        final String id = node.getProperty("id");
        final String[] idParts = StringUtils.split(id, ":", 2);
        if (idParts != null) {
            switch (idParts[0]) {
                case "UniProtKB":
                    description.addIdentifier(IdentifierType.UNIPROT_KB, idParts[1]);
                    break;
                case "ComplexPortal":
                    description.addIdentifier("ComplexPortal", idParts[1]);
                    break;
                case "RNAcentral":
                    description.addIdentifier("RNAcentral", idParts[1]);
                    break;
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] createRNAMappingDescription(final Node node,
                                                                 final RNANodeMappingDescription.RNAType rnaType) {
        return populateDescription(node, new RNANodeMappingDescription(rnaType));
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{GeneOntologyGraphExporter.DB_OBJECT_LABEL};
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
