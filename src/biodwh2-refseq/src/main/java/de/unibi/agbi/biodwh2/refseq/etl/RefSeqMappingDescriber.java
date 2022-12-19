package de.unibi.agbi.biodwh2.refseq.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class RefSeqMappingDescriber extends MappingDescriber {
    public RefSeqMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        switch (localMappingLabel) {
            case RefSeqGraphExporter.GENE_LABEL:
                return describeGene(node);
            case RefSeqGraphExporter.M_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.M_RNA);
            case RefSeqGraphExporter.T_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.T_RNA);
            case RefSeqGraphExporter.R_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.R_RNA);
            case RefSeqGraphExporter.NC_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.UNKNOWN); // TODO
            case RefSeqGraphExporter.PRIMARY_TRANSCRIPT_LABEL:
            case RefSeqGraphExporter.MI_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.MI_RNA);
            case RefSeqGraphExporter.SN_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.SN_RNA);
            case RefSeqGraphExporter.SC_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.SC_RNA);
            case RefSeqGraphExporter.SNO_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.SNO_RNA);
            case RefSeqGraphExporter.LNC_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.LNC_RNA);
            case RefSeqGraphExporter.VAULT_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.VT_RNA);
            case RefSeqGraphExporter.Y_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.Y_RNA);
            case RefSeqGraphExporter.ANTISENSE_RNA_LABEL:
                return describeRNA(node, RNANodeMappingDescription.RNAType.ANTISENSE_RNA);
            case RefSeqGraphExporter.CDS_LABEL:
                return describeCDS(node);
        }
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] xrefParts = StringUtils.split(xref, ":", 2);
                switch (xrefParts[0]) {
                    case "HGNC":
                        final String hgncId = StringUtils.replace(xrefParts[1], "HGNC:", "");
                        description.addIdentifier(IdentifierType.HGNC_ID, Integer.parseInt(hgncId));
                        break;
                    case "MIM":
                        description.addIdentifier(IdentifierType.OMIM, Integer.parseInt(xrefParts[1]));
                        break;
                    case "GeneID":
                        description.addIdentifier(IdentifierType.NCBI_GENE, Integer.parseInt(xrefParts[1]));
                        break;
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeRNA(final Node node, RNANodeMappingDescription.RNAType rnaType) {
        final NodeMappingDescription description = new RNANodeMappingDescription(rnaType);
        description.addName(node.getProperty("name"));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] xrefParts = StringUtils.split(xref, ":", 2);
                switch (xrefParts[0]) {
                    case "Genbank":
                        description.addIdentifier(IdentifierType.GENBANK, removeGenbankIdVersion(xrefParts[1]));
                        break;
                    case "miRBase":
                        description.addIdentifier(IdentifierType.MIRBASE.name(), xrefParts[1]);
                        break;
                    case "Ensembl":
                        description.addIdentifier(IdentifierType.ENSEMBL, xrefParts[1]);
                        break;
                }
            }
        }
        return new NodeMappingDescription[]{description};
    }

    private String removeGenbankIdVersion(final String id) {
        final int indexOfVersionDot = id.lastIndexOf('.');
        return indexOfVersionDot == -1 ? id : id.substring(0, indexOfVersionDot);
    }

    private NodeMappingDescription[] describeCDS(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("name"));
        final String[] xrefs = node.getProperty("xrefs");
        if (xrefs != null) {
            for (final String xref : xrefs) {
                final String[] xrefParts = StringUtils.split(xref, ":", 2);
                switch (xrefParts[0]) {
                    case "Genbank":
                        description.addIdentifier(IdentifierType.GENBANK, removeGenbankIdVersion(xrefParts[1]));
                        break;
                    case "Ensembl":
                        description.addIdentifier(IdentifierType.ENSEMBL, xrefParts[1]);
                        break;
                }
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
        return new String[]{
                RefSeqGraphExporter.GENE_LABEL, RefSeqGraphExporter.M_RNA_LABEL, RefSeqGraphExporter.T_RNA_LABEL,
                RefSeqGraphExporter.R_RNA_LABEL, RefSeqGraphExporter.NC_RNA_LABEL, RefSeqGraphExporter.MI_RNA_LABEL,
                RefSeqGraphExporter.SN_RNA_LABEL, RefSeqGraphExporter.SC_RNA_LABEL, RefSeqGraphExporter.SNO_RNA_LABEL,
                RefSeqGraphExporter.LNC_RNA_LABEL, RefSeqGraphExporter.VAULT_RNA_LABEL, RefSeqGraphExporter.Y_RNA_LABEL,
                RefSeqGraphExporter.CDS_LABEL, RefSeqGraphExporter.PRIMARY_TRANSCRIPT_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
