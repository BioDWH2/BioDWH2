package de.unibi.agbi.biodwh2.sider.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;
import de.unibi.agbi.biodwh2.sider.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * MedDRA concept types are hierarchical: PT - Represents a single medical concept LLT - Lowest level of the
 * terminology, related to a single PT as a synonym, lexical variant, or quasisynonym (Note: All PTs have an identical
 * LLT)
 * <p>
 * Compound ids are in the old STITCH format with CID0 for stereo and CID1 for flat (merged) compounds. The newer format
 * changed to CIDs (single) and CIDm (merged). More info: https://www.biostars.org/p/155342/
 */
public class SiderGraphExporter extends GraphExporter<SiderDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiderGraphExporter.class);
    private static final String MEDDRA_TERM_LABEL = "MeddraTerm";
    private static final String UMLS_ID_KEY = "umls_id";
    private static final String MEDDRA_ID_KEY = "meddra_id";
    private static final String FLAT_ID_KEY = "flat_id";
    private static final String STEREO_ID_KEY = "stereo_id";
    private static final String ATC_CODES_KEY = "atc_codes";

    private final Set<Long> edgeHashCache = new HashSet<>();

    public SiderGraphExporter(final SiderDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Drug", FLAT_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode("Drug", STEREO_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(MEDDRA_TERM_LABEL, UMLS_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(MEDDRA_TERM_LABEL, MEDDRA_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drug names...");
        final Map<String, Long> drugIdNodeIdMap = addAllDrugNames(workspace, graph);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drug atc codes...");
        addAllDrugAtcCodes(workspace, graph, drugIdNodeIdMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting indications...");
        final Map<String, Long> meddraIdNodeIdMap = new HashMap<>();
        addAllIndications(workspace, graph, drugIdNodeIdMap, meddraIdNodeIdMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting side effects...");
        addAllSideEffects(workspace, graph, drugIdNodeIdMap, meddraIdNodeIdMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting side effect frequencies...");
        addAllSideEffectFrequencies(workspace, graph, drugIdNodeIdMap, meddraIdNodeIdMap);
        edgeHashCache.clear();
        return true;
    }

    private Map<String, Long> addAllDrugNames(final Workspace workspace, final Graph graph) throws ExporterException {
        final Map<String, Long> drugIdNodeIdMap = new HashMap<>();
        for (final DrugName drug : parseTsvFile(workspace, SiderUpdater.DRUG_NAMES_FILE_NAME, DrugName.class)) {
            final Node node = graph.addNodeFromModel(drug);
            drugIdNodeIdMap.put(drug.id, node.getId());
        }
        return drugIdNodeIdMap;
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final String fileName,
                                         final Class<T> typeClass) throws ExporterException {
        try {
            final MappingIterator<T> iterator = FileUtils.openTsv(workspace, dataSource, fileName, typeClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void addAllDrugAtcCodes(final Workspace workspace, final Graph graph,
                                    final Map<String, Long> drugIdNodeIdMap) throws ExporterException {
        for (final DrugAtc atcCode : parseTsvFile(workspace, SiderUpdater.DRUG_ATC_FILE_NAME, DrugAtc.class)) {
            final Node drugNode = graph.getNode(drugIdNodeIdMap.get(atcCode.id));
            addValueToNodePropertySet(drugNode, ATC_CODES_KEY, atcCode.atc);
            graph.update(drugNode);
        }
    }

    private static void addValueToNodePropertySet(final Node node, final String key, final String value) {
        final Set<String> values = new HashSet<>();
        if (node.hasProperty(key))
            Collections.addAll(values, node.getProperty(key));
        values.add(value);
        node.setProperty(key, values.toArray(new String[0]));
    }

    private void addAllIndications(final Workspace workspace, final Graph graph,
                                   final Map<String, Long> drugIdNodeIdMap,
                                   final Map<String, Long> meddraIdNodeIdMap) throws ExporterException {
        edgeHashCache.clear();
        for (Indication indication : parseGzipTsvFile(workspace, SiderUpdater.INDICATIONS_FILE_NAME,
                                                      Indication.class)) {
            long meddraTermNodeId = getOrAddMeddraTermNode(graph, meddraIdNodeIdMap, indication.umlsConceptId,
                                                           indication.conceptName, indication.meddraUmlsConceptId,
                                                           indication.meddraConceptName);
            long drugNodeId = getOrAddDrugNode(graph, indication.flatCompoundId, indication.stereoCompoundId,
                                               drugIdNodeIdMap);
            long hash = Objects.hash(meddraTermNodeId, drugNodeId, indication.label, indication.detectionMethod);
            if (!edgeHashCache.contains(hash)) {
                graph.addEdge(drugNodeId, meddraTermNodeId, "INDICATES", "label", indication.label, "detection_method",
                              indication.detectionMethod);
                edgeHashCache.add(hash);
            }
        }
    }

    private long getOrAddMeddraTermNode(final Graph graph, final Map<String, Long> meddraIdNodeIdMap,
                                        final String umlsConceptId, final String umlsConceptName,
                                        final String conceptId, final String conceptName) {
        if (meddraIdNodeIdMap.containsKey(conceptId))
            return meddraIdNodeIdMap.get(conceptId);
        Node node;
        if (umlsConceptName == null)
            node = graph.addNode(MEDDRA_TERM_LABEL, MEDDRA_ID_KEY, conceptId, UMLS_ID_KEY, umlsConceptId, "meddra_name",
                                 conceptName);
        else
            node = graph.addNode(MEDDRA_TERM_LABEL, MEDDRA_ID_KEY, conceptId, UMLS_ID_KEY, umlsConceptId, "meddra_name",
                                 conceptName, "umls_name", umlsConceptName);
        meddraIdNodeIdMap.put(conceptId, node.getId());
        return node.getId();
    }

    private long getOrAddDrugNode(final Graph graph, final String flatId, final String stereoId,
                                  final Map<String, Long> drugIdNodeIdMap) {
        Long nodeId = drugIdNodeIdMap.get(flatId);
        if (nodeId == null)
            nodeId = drugIdNodeIdMap.get(stereoId);
        if (nodeId != null) {
            final Node node = graph.getNode(nodeId);
            if (!node.hasProperty(STEREO_ID_KEY)) {
                node.setProperty(STEREO_ID_KEY, stereoId);
                graph.update(node);
                drugIdNodeIdMap.put(stereoId, node.getId());
            }
            return nodeId;
        }
        final Node node = graph.addNode("Drug", FLAT_ID_KEY, flatId, STEREO_ID_KEY, stereoId);
        drugIdNodeIdMap.put(flatId, node.getId());
        drugIdNodeIdMap.put(stereoId, node.getId());
        return node.getId();
    }

    private <T> Iterable<T> parseGzipTsvFile(final Workspace workspace, final String fileName,
                                             final Class<T> typeClass) throws ExporterException {
        try {
            final MappingIterator<T> iterator = FileUtils.openGzipTsv(workspace, dataSource, fileName, typeClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void addAllSideEffects(final Workspace workspace, final Graph graph,
                                   final Map<String, Long> drugIdNodeIdMap,
                                   final Map<String, Long> meddraIdNodeIdMap) throws ExporterException {
        edgeHashCache.clear();
        for (SideEffect sideEffect : parseGzipTsvFile(workspace, SiderUpdater.SIDE_EFFECTS_FILE_NAME,
                                                      SideEffect.class)) {
            long meddraTermNodeId = getOrAddMeddraTermNode(graph, meddraIdNodeIdMap, sideEffect.umlsConceptId, null,
                                                           sideEffect.meddraUmlsConceptId, sideEffect.sideEffectName);
            long drugNodeId = getOrAddDrugNode(graph, sideEffect.flatCompoundId, sideEffect.stereoCompoundId,
                                               drugIdNodeIdMap);
            long hash = Objects.hash(meddraTermNodeId, drugNodeId, sideEffect.label);
            if (!edgeHashCache.contains(hash)) {
                graph.addEdge(drugNodeId, meddraTermNodeId, "HAS_SIDE_EFFECT", "label", sideEffect.label);
                edgeHashCache.add(hash);
            }
        }
    }

    private void addAllSideEffectFrequencies(final Workspace workspace, final Graph graph,
                                             final Map<String, Long> drugIdNodeIdMap,
                                             final Map<String, Long> meddraIdNodeIdMap) throws ExporterException {
        edgeHashCache.clear();
        for (Frequency frequency : parseGzipTsvFile(workspace, SiderUpdater.FREQUENCIES_FILE_NAME, Frequency.class)) {
            long meddraTermNodeId = getOrAddMeddraTermNode(graph, meddraIdNodeIdMap, frequency.umlsConceptId, null,
                                                           frequency.meddraUmlsConceptId, frequency.sideEffectName);
            long drugNodeId = getOrAddDrugNode(graph, frequency.flatCompoundId, frequency.stereoCompoundId,
                                               drugIdNodeIdMap);
            long hash = Objects.hash(meddraTermNodeId, drugNodeId, frequency.frequency, frequency.frequencyLowerBound,
                                     frequency.frequencyUpperBound, frequency.placebo);
            if (!edgeHashCache.contains(hash)) {
                EdgeBuilder builder = graph.buildEdge();
                builder.fromNode(drugNodeId);
                builder.toNode(meddraTermNodeId);
                builder.withLabel("HAS_SIDE_EFFECT_FREQUENCY");
                builder.withProperty("frequency", frequency.frequency);
                builder.withProperty("frequency_lower_bound", frequency.frequencyLowerBound);
                builder.withProperty("frequency_upper_bound", frequency.frequencyUpperBound);
                if (frequency.placebo != null)
                    builder.withProperty("placebo", frequency.placebo.equalsIgnoreCase("placebo"));
                builder.build();
                edgeHashCache.add(hash);
            }
        }
    }
}
