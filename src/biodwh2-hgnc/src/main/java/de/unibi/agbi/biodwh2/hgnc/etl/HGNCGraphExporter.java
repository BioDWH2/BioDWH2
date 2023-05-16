package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;
import org.apache.commons.lang3.StringUtils;

public class HGNCGraphExporter extends GraphExporter<HGNCDataSource> {
    private static final String CODING_LOCUS_GROUP = "protein-coding gene";
    private static final String NON_CODING_LOCUS_GROUP = "non-coding RNA";
    // Other locus types: "RNA, cluster", "RNA, misc", "RNA, vault", "RNA, Y"
    private static final String LNC_RNA_LOCUS_TYPE = "RNA, long non-coding";
    private static final String MI_RNA_LOCUS_TYPE = "RNA, micro";
    private static final String T_RNA_LOCUS_TYPE = "RNA, transfer";
    private static final String R_RNA_LOCUS_TYPE = "RNA, ribosomal";
    private static final String SN_RNA_LOCUS_TYPE = "RNA, small nuclear";
    private static final String SNO_RNA_LOCUS_TYPE = "RNA, small nucleolar";
    static final String GENE_LABEL = "Gene";
    static final String PROTEIN_LABEL = "Protein";
    static final String LNC_RNA_LABEL = "lncRNA";
    static final String MI_RNA_LABEL = "miRNA";
    static final String T_RNA_LABEL = "tRNA";
    static final String R_RNA_LABEL = "rRNA";
    static final String SN_RNA_LABEL = "snRNA";
    static final String SNO_RNA_LABEL = "snoRNA";
    static final String TRANSCRIBES_TO_LABEL = "TRANSCRIBES_TO";
    static final String TRANSLATES_TO_LABEL = "TRANSLATES_TO";
    static final String UNIPROT_ID_KEY = "uniprot_id";

    public HGNCGraphExporter(final HGNCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 5;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "symbol", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(MI_RNA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, UNIPROT_ID_KEY, IndexDescription.Type.UNIQUE));
        for (final Gene gene : dataSource.genes) {
            final Node node = graph.addNodeFromModel(gene);
            if (CODING_LOCUS_GROUP.equalsIgnoreCase(gene.locusGroup))
                exportGeneProteins(graph, gene, node);
            else if (NON_CODING_LOCUS_GROUP.equalsIgnoreCase(gene.locusGroup))
                exportGeneRNA(graph, gene, node);
        }
        return true;
    }

    private void exportGeneProteins(final Graph graph, final Gene gene, final Node geneNode) {
        if (StringUtils.isNotEmpty(gene.uniprotIds)) {
            final String[] uniprotIds = StringUtils.split(gene.uniprotIds, '|');
            for (final String uniProtId : uniprotIds) {
                final Node proteinNode = getOrCreateProteinNode(graph, uniProtId);
                graph.addEdge(geneNode, proteinNode, TRANSLATES_TO_LABEL);
            }
        }
    }

    private Node getOrCreateProteinNode(final Graph graph, final String uniprotId) {
        Node node = graph.findNode(PROTEIN_LABEL, UNIPROT_ID_KEY, uniprotId);
        if (node == null)
            node = graph.addNode(PROTEIN_LABEL, UNIPROT_ID_KEY, uniprotId);
        return node;
    }

    private void exportGeneRNA(final Graph graph, final Gene gene, final Node geneNode) {
        Node rnaNode = null;
        if (MI_RNA_LOCUS_TYPE.equalsIgnoreCase(gene.locusType))
            rnaNode = graph.addNode(MI_RNA_LABEL, "mirbase_accession", gene.mirbase);
        /*
        else if (LNC_RNA_LOCUS_TYPE.equalsIgnoreCase(gene.locusType))
            rnaNode = graph.addNode(LNC_RNA_LABEL);
        else if (T_RNA_LOCUS_TYPE.equalsIgnoreCase(gene.locusType))
            rnaNode = graph.addNode(T_RNA_LABEL);
        else if (R_RNA_LOCUS_TYPE.equalsIgnoreCase(gene.locusType))
            rnaNode = graph.addNode(R_RNA_LABEL);
        else if (SN_RNA_LOCUS_TYPE.equalsIgnoreCase(gene.locusType))
            rnaNode = graph.addNode(SN_RNA_LABEL);
        else if (SNO_RNA_LOCUS_TYPE.equalsIgnoreCase(gene.locusType))
            rnaNode = graph.addNode(SNO_RNA_LABEL);
         */
        if (rnaNode != null)
            graph.addEdge(geneNode, rnaNode, TRANSCRIBES_TO_LABEL);
    }
}
