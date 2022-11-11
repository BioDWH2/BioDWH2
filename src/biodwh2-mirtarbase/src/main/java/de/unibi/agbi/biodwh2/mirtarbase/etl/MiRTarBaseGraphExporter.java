package de.unibi.agbi.biodwh2.mirtarbase.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.XlsxMappingIterator;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.mirtarbase.MiRTarBaseDataSource;
import de.unibi.agbi.biodwh2.mirtarbase.model.MTIEntry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MiRTarBaseGraphExporter extends GraphExporter<MiRTarBaseDataSource> {
    private static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";
    private static final String HAS_EVIDENCE_LABEL = "HAS_EVIDENCE";
    static final String MIRNA_LABEL = "miRNA";
    static final String GENE_LABEL = "Gene";
    static final String PUBLICATION_LABEL = "Publication";
    private static final String INTERACTION_LABEL = "Interaction";
    private static final String ID_KEY = "id";
    private static final String SPECIES_KEY = "species";

    public MiRTarBaseGraphExporter(final MiRTarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(MIRNA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(INTERACTION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        exportInteractions(workspace, graph);
        return true;
    }

    private void exportInteractions(final Workspace workspace, final Graph graph) {
        final Map<Long, Set<Long>> addedInteractionEdges = new HashMap<>();
        final Map<String, Long> geneKeyNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay(HAS_EVIDENCE_LABEL);
        graph.beginEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        try (final InputStream inputStream = FileUtils.openInput(workspace, dataSource, "miRTarBase_MTI.xlsx");
             final XlsxMappingIterator<MTIEntry> iterator = new XlsxMappingIterator<>(MTIEntry.class, inputStream)) {
            while (iterator.hasNext())
                exportInteraction(graph, addedInteractionEdges, geneKeyNodeIdMap, iterator.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        graph.endEdgeIndicesDelay(HAS_EVIDENCE_LABEL);
        graph.endEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
    }

    private void exportInteraction(final Graph graph, final Map<Long, Set<Long>> addedInteractionEdges,
                                   final Map<String, Long> geneKeyNodeIdMap, final MTIEntry entry) {
        final Node interactionNode = getOrCreateInteractionNode(graph, entry.miRTarBaseId);
        final Node miRNANode = getOrCreateMiRNANode(graph, entry.miRNA, entry.speciesMiRNA);
        final Long geneNodeId = getOrCreateGeneNode(graph, geneKeyNodeIdMap, entry.targetGene, entry.speciesTargetGene,
                                                    entry.targetGeneEntrezId);
        final Node publicationNode = getOrCreatePublicationNode(graph, entry.references);
        graph.addEdge(interactionNode, publicationNode, HAS_EVIDENCE_LABEL, "experiments",
                      StringUtils.splitByWholeSeparator(entry.experiments, "//"), "support_type", entry.supportType);
        final Set<Long> edgeIds = addedInteractionEdges.computeIfAbsent(interactionNode.getId(), k -> new HashSet<>());
        if (!edgeIds.contains(miRNANode.getId())) {
            graph.addEdge(miRNANode, interactionNode, ASSOCIATED_WITH_LABEL);
            edgeIds.add(miRNANode.getId());
        }
        if (!edgeIds.contains(geneNodeId)) {
            graph.addEdge(geneNodeId, interactionNode, ASSOCIATED_WITH_LABEL);
            edgeIds.add(geneNodeId);
        }
    }

    private Node getOrCreateInteractionNode(final Graph graph, final String id) {
        Node node = graph.findNode(INTERACTION_LABEL, ID_KEY, id);
        if (node == null)
            node = graph.addNode(INTERACTION_LABEL, ID_KEY, id);
        return node;
    }

    private Node getOrCreateMiRNANode(final Graph graph, final String id, final String species) {
        Node node = graph.findNode(MIRNA_LABEL, ID_KEY, id);
        if (node == null)
            node = graph.addNode(MIRNA_LABEL, ID_KEY, id, SPECIES_KEY, species);
        return node;
    }

    private Long getOrCreateGeneNode(final Graph graph, final Map<String, Long> geneKeyNodeIdMap, final String id,
                                     final String species, final Integer entrezGeneId) {
        final String key = species + '_' + id;
        Long nodeId = geneKeyNodeIdMap.get(key);
        if (nodeId == null) {
            nodeId = graph.addNode(GENE_LABEL, ID_KEY, id, SPECIES_KEY, species, "entrez_gene_id", entrezGeneId)
                          .getId();
            geneKeyNodeIdMap.put(key, nodeId);
        }
        return nodeId;
    }

    private Node getOrCreatePublicationNode(final Graph graph, final Integer pmid) {
        Node node = graph.findNode(PUBLICATION_LABEL, "pmid", pmid);
        if (node == null)
            node = graph.addNode(PUBLICATION_LABEL, "pmid", pmid);
        return node;
    }
}
