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
    static final String GENE_LABEL = "Gene";
    static final String PROTEIN_LABEL = "Protein";
    static final String CODES_FOR_LABEL = "CODES_FOR";
    static final String UNIPROT_ID_KEY = "uniprot_id";
    static final String HGNC_ID_KEY = "hgnc_id";

    public HGNCGraphExporter(final HGNCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, HGNC_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "symbol", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, UNIPROT_ID_KEY, IndexDescription.Type.UNIQUE));
        for (final Gene gene : dataSource.genes) {
            final Node node = graph.addNodeFromModel(gene);
            exportGeneProteins(graph, gene, node);
        }
        return true;
    }

    private void exportGeneProteins(final Graph graph, final Gene gene, final Node geneNode) {
        if (StringUtils.isNotEmpty(gene.uniprotIds)) {
            final String[] uniprotIds = StringUtils.split(gene.uniprotIds, '|');
            for (final String uniProtId : uniprotIds) {
                final Node proteinNode = getOrCreateProteinNode(graph, uniProtId);
                graph.addEdge(geneNode, proteinNode, CODES_FOR_LABEL);
            }
        }
    }

    private Node getOrCreateProteinNode(final Graph graph, final String uniprotId) {
        Node node = graph.findNode(PROTEIN_LABEL, UNIPROT_ID_KEY, uniprotId);
        if (node == null)
            node = graph.addNode(PROTEIN_LABEL, UNIPROT_ID_KEY, uniprotId);
        return node;
    }
}
