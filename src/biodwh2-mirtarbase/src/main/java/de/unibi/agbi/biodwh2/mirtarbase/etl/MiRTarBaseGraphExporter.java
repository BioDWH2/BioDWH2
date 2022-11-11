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
import de.unibi.agbi.biodwh2.mirtarbase.model.MicroRNATargetSite;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MiRTarBaseGraphExporter extends GraphExporter<MiRTarBaseDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiRTarBaseGraphExporter.class);
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
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Collecting interaction target sites...");
        final Map<String, Map<Integer, Map<String, Map<String, String>>>> targetSiteMap = collectTargetSites(workspace);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting interactions...");
        exportInteractions(workspace, graph, targetSiteMap);
        return true;
    }

    private Map<String, Map<Integer, Map<String, Map<String, String>>>> collectTargetSites(final Workspace workspace) {
        final Map<String, Map<Integer, Map<String, Map<String, String>>>> targetSiteMap = new HashMap<>();
        try (final InputStream inputStream = FileUtils.openInput(workspace, dataSource, "MicroRNA_Target_Sites.xlsx");
             final XlsxMappingIterator<MicroRNATargetSite> iterator = new XlsxMappingIterator<>(
                     MicroRNATargetSite.class, inputStream)) {
            while (iterator.hasNext()) {
                final MicroRNATargetSite entry = iterator.next();
                final Map<Integer, Map<String, Map<String, String>>> pmidExperimentMap = targetSiteMap.computeIfAbsent(
                        entry.miRTarBaseId, k -> new HashMap<>());
                final Map<String, Map<String, String>> experimentSupportTypeMap = pmidExperimentMap.computeIfAbsent(
                        entry.references, k -> new HashMap<>());
                final Map<String, String> supportTypeSequenceMap = experimentSupportTypeMap.computeIfAbsent(
                        entry.experiments, k -> new HashMap<>());
                supportTypeSequenceMap.put(entry.supportType, entry.targetSite);
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return targetSiteMap;
    }

    private void exportInteractions(final Workspace workspace, final Graph graph,
                                    final Map<String, Map<Integer, Map<String, Map<String, String>>>> targetSiteMap) {
        final Map<Long, Set<Long>> addedInteractionEdges = new HashMap<>();
        final Map<String, Long> geneKeyNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay(HAS_EVIDENCE_LABEL);
        graph.beginEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        try (final InputStream inputStream = FileUtils.openInput(workspace, dataSource, "miRTarBase_MTI.xlsx");
             final XlsxMappingIterator<MTIEntry> iterator = new XlsxMappingIterator<>(MTIEntry.class, inputStream)) {
            while (iterator.hasNext())
                exportInteraction(graph, targetSiteMap, addedInteractionEdges, geneKeyNodeIdMap, iterator.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        graph.endEdgeIndicesDelay(HAS_EVIDENCE_LABEL);
        graph.endEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
    }

    private void exportInteraction(final Graph graph,
                                   final Map<String, Map<Integer, Map<String, Map<String, String>>>> targetSiteMap,
                                   final Map<Long, Set<Long>> addedInteractionEdges,
                                   final Map<String, Long> geneKeyNodeIdMap, final MTIEntry entry) {
        final Node interactionNode = getOrCreateInteractionNode(graph, entry.miRTarBaseId);
        final Node miRNANode = getOrCreateMiRNANode(graph, entry.miRNA, entry.speciesMiRNA);
        final Long geneNodeId = getOrCreateGeneNode(graph, geneKeyNodeIdMap, entry.targetGene, entry.speciesTargetGene,
                                                    entry.targetGeneEntrezId);
        final Node publicationNode = getOrCreatePublicationNode(graph, entry.references);
        final String targetSite = getTargetSiteForEntry(targetSiteMap, entry);
        if (targetSite != null) {
            graph.addEdge(interactionNode, publicationNode, HAS_EVIDENCE_LABEL, "experiments",
                          StringUtils.splitByWholeSeparator(entry.experiments, "//"), "support_type", entry.supportType,
                          "target_site", targetSite);
        } else {
            graph.addEdge(interactionNode, publicationNode, HAS_EVIDENCE_LABEL, "experiments",
                          StringUtils.splitByWholeSeparator(entry.experiments, "//"), "support_type",
                          entry.supportType);
        }
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

    private String getTargetSiteForEntry(
            final Map<String, Map<Integer, Map<String, Map<String, String>>>> targetSiteMap, final MTIEntry entry) {
        final Map<Integer, Map<String, Map<String, String>>> pmidExperimentMap = targetSiteMap.get(entry.miRTarBaseId);
        if (pmidExperimentMap == null)
            return null;
        final Map<String, Map<String, String>> experimentSupportTypeMap = pmidExperimentMap.get(entry.references);
        if (experimentSupportTypeMap == null)
            return null;
        final Map<String, String> supportTypeSequenceMap = experimentSupportTypeMap.get(entry.experiments);
        if (supportTypeSequenceMap == null)
            return null;
        return supportTypeSequenceMap.get(entry.supportType);
    }
}
