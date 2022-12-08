package de.unibi.agbi.biodwh2.refseq.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.refseq.RefSeqDataSource;
import de.unibi.agbi.biodwh2.refseq.model.Feature;

import java.io.IOException;

public class RefSeqGraphExporter extends GraphExporter<RefSeqDataSource> {
    private static final String GENE_LABEL = "Gene";

    public RefSeqGraphExporter(final RefSeqDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Assembly", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Chromosome", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        // TODO: dynamic file name
        try (final MappingIterator<Feature> features = FileUtils.openGzipTsv(workspace, dataSource,
                                                                             "GCF_000001405.40_GRCh38.p14_feature_table.txt.gz",
                                                                             Feature.class)) {
            while (features.hasNext())
                exportFeature(graph, features.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportFeature(final Graph graph, final Feature feature) {
        final Node assemblyNode = getOrCreateAssemblyNode(graph, feature);
        final Node chromosomeNode = getOrCreateChromosomeNode(graph, feature, assemblyNode);
        final Node geneNode;
        if ("gene".equals(feature.feature))
            geneNode = getOrCreateGeneNode(graph, feature, chromosomeNode);
        else {
            geneNode = graph.findNode(GENE_LABEL, ID_KEY, feature.geneId);
            // TODO: C_region, D_segment, J_segment, V_segment, CDS, misc_RNA, mRNA, ncRNA, precursor_RNA, rRNA, tRNA
        }
    }

    private Node getOrCreateAssemblyNode(final Graph graph, final Feature feature) {
        Node node = graph.findNode("Assembly", ID_KEY, feature.assembly);
        if (node == null)
            node = graph.addNode("Assembly", ID_KEY, feature.assembly, "unit", feature.assemblyUnit);
        return node;
    }

    private Node getOrCreateChromosomeNode(final Graph graph, final Feature feature, final Node assemblyNode) {
        Node node = graph.findNode("Chromosome", ID_KEY, feature.genomicAccession);
        if (node == null) {
            node = graph.addNode("Chromosome", ID_KEY, feature.genomicAccession, "name", feature.chromosome, "type",
                                 feature.seqType);
            graph.addEdge(assemblyNode, node, "HAS");
        }
        return node;
    }

    private Node getOrCreateGeneNode(final Graph graph, final Feature feature, final Node chromosomeNode) {
        Node node = graph.findNode(GENE_LABEL, ID_KEY, feature.geneId);
        final Edge locatedInEdge;
        if (node != null) {
            locatedInEdge = graph.findEdge("LOCATED_IN", Edge.FROM_ID_FIELD, feature.geneId, Edge.TO_ID_FIELD,
                                           chromosomeNode.getId());
        } else {
            locatedInEdge = null;
            // TODO: attributes (pseudo)
            node = graph.addNode(GENE_LABEL, ID_KEY, feature.geneId, "name", feature.name, "symbol", feature.symbol,
                                 "class", feature.featureClass);
        }
        if (locatedInEdge == null) {
            graph.addEdge(node, chromosomeNode, "LOCATED_IN", "start", feature.start, "end", feature.end, "strand",
                          feature.strand, "interval_length", feature.featureIntervalLength);
        }
        return node;
    }
}
