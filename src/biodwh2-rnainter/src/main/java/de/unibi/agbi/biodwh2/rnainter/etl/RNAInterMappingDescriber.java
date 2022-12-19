package de.unibi.agbi.biodwh2.rnainter.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class RNAInterMappingDescriber extends MappingDescriber {
    public RNAInterMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (RNAInterGraphExporter.RNA_LABEL.equals(localMappingLabel))
            return describeRNA(node);
        if (RNAInterGraphExporter.COMPOUND_LABEL.equals(localMappingLabel))
            return describeCompound(node);
        if (RNAInterGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (RNAInterGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription[] describeRNA(final Node node) {
        final String type = node.getProperty(RNAInterGraphExporter.TYPE_KEY);
        if (type != null) {
            switch (type) {
                case "mRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.M_RNA);
                case "rRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.R_RNA);
                case "tRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.T_RNA);
                case "miRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.MI_RNA);
                case "snRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.SN_RNA);
                case "scRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.SC_RNA);
                case "lncRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.LNC_RNA);
                case "sncRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.SNC_RNA);
                case "snoRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.SNO_RNA);
                case "vtRNAs":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.VT_RNA);
                case "eRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.E_RNA);
                case "piRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.PI_RNA);
                case "ribozyme":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.RIBOZYME);
                case "ncRNA":
                    return describeRNA(node, RNANodeMappingDescription.RNAType.UNKNOWN); // TODO
                case "misc_RNA":
                case "miscRNA":
                case "circRNA":
                case "lincRNA":
                case "scaRNA":
                case "shRNA":
                case "sRNA":
                case "Mt_tRNA":
                case "antisense":
                case "non_stop_decay":
                case "nonsense_mediated_decay":
                case "PCG":
                case "processed_transcript":
                case "pseudo":
                case "repeats":
                case "retained_intron":
                case "TEC":
                case "TR_C_gene":
                case "tRF":
                case "unassigned RNA":
                case "unknown":
                case "others":
                    // TODO
                    break;
            }
        }
        return null;
    }

    private NodeMappingDescription[] describeRNA(final Node node, final RNANodeMappingDescription.RNAType rnaType) {
        final NodeMappingDescription description = new RNANodeMappingDescription(rnaType);
        final String id = node.getProperty("id");
        if (id != null) {
            final String[] idParts = StringUtils.split(id, ":", 2);
            switch (idParts[0]) {
                case "NCBI":
                    description.addIdentifier(IdentifierType.NCBI_GENE, Integer.parseInt(idParts[1]));
                    break;
                case "miRBase":
                    description.addIdentifier(IdentifierType.MIRBASE, idParts[1]);
                    break;
                case "Ensembl":
                    description.addIdentifier(IdentifierType.ENSEMBL, idParts[1]);
                    break;
            }
        }
        description.addName(node.getProperty(RNAInterGraphExporter.NAME_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeCompound(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        final String id = node.getProperty("id");
        if (id != null) {
            final String[] idParts = StringUtils.split(id, ":", 2);
            switch (idParts[0]) {
                case "CID":
                    description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, Integer.parseInt(idParts[1]));
                    break;
                case "DrugBank":
                    description.addIdentifier(IdentifierType.DRUG_BANK, idParts[1]);
                    break;
            }
        }
        description.addName(node.getProperty(RNAInterGraphExporter.NAME_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        final String id = node.getProperty("id");
        if (id != null) {
            final String[] idParts = StringUtils.split(id, ":", 2);
            switch (idParts[0]) {
                case "NCBI":
                    description.addIdentifier(IdentifierType.NCBI_GENE, Integer.parseInt(idParts[1]));
                    break;
                case "UniProt":
                    description.addIdentifier(IdentifierType.UNIPROT_KB, idParts[1]);
                    break;
            }
        }
        description.addName(node.getProperty(RNAInterGraphExporter.NAME_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        final String id = node.getProperty("id");
        if (id != null) {
            final String[] idParts = StringUtils.split(id, ":", 2);
            if (idParts[0].equals("NCBI"))
                description.addIdentifier(IdentifierType.NCBI_GENE, Integer.parseInt(idParts[1]));
        }
        description.addName(node.getProperty(RNAInterGraphExporter.NAME_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                RNAInterGraphExporter.RNA_LABEL, RNAInterGraphExporter.COMPOUND_LABEL,
                RNAInterGraphExporter.PROTEIN_LABEL, RNAInterGraphExporter.GENE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
