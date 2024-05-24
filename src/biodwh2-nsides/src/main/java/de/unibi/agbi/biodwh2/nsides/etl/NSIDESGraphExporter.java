package de.unibi.agbi.biodwh2.nsides.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.nsides.NSIDESDataSource;
import de.unibi.agbi.biodwh2.nsides.model.OffsidesEntry;
import de.unibi.agbi.biodwh2.nsides.model.TwosidesEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NSIDESGraphExporter extends GraphExporter<NSIDESDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(NSIDESGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String DRUG_EFFECT_LABEL = "DrugEffect";
    static final String PART_OF_LABEL = "PART_OF";
    static final String HAS_EFFECT_LABEL = "HAS_EFFECT";

    public NSIDESGraphExporter(final NSIDESDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DRUG_EFFECT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, Long> rxnormIdNodeIdMap = new HashMap<>();
        final Map<String, Long> meddraIdNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay(HAS_EFFECT_LABEL);
        exportOffsidesEntries(workspace, graph, rxnormIdNodeIdMap, meddraIdNodeIdMap);
        graph.endEdgeIndicesDelay(HAS_EFFECT_LABEL);
        exportTwosidesEntries(workspace, graph, rxnormIdNodeIdMap, meddraIdNodeIdMap);
        return true;
    }

    private void exportOffsidesEntries(final Workspace workspace, final Graph graph,
                                       final Map<String, Long> rxnormIdNodeIdMap,
                                       final Map<String, Long> meddraIdNodeIdMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting OFFSIDES...");
        try (final var zipStream = FileUtils.openGzip(workspace, dataSource, NSIDESUpdater.OFFSIDES_FILE_NAME)) {
            final MappingIterator<OffsidesEntry> entries = FileUtils.openCsvWithHeader(zipStream, OffsidesEntry.class);
            while (entries.hasNext())
                exportOffsidesEntry(graph, entries.next(), rxnormIdNodeIdMap, meddraIdNodeIdMap);
        } catch (IOException e) {
            throw new ExporterException(e);
        }
    }

    private void exportOffsidesEntry(final Graph graph, final OffsidesEntry entry,
                                     final Map<String, Long> rxnormIdNodeIdMap,
                                     final Map<String, Long> meddraIdNodeIdMap) {
        if ("A".equals(entry.a))
            return;
        final long drugNodeId = getOrCreateDrugNode(graph, rxnormIdNodeIdMap, entry.drugRxnormId,
                                                    entry.drugConceptName);
        final long drugEffectNodeId = getOrCreateDrugEffectNode(graph, meddraIdNodeIdMap, entry.conditionMeddraId,
                                                                entry.conditionConceptName);
        final EdgeBuilder builder = graph.buildEdge().fromNode(drugNodeId).toNode(drugEffectNodeId).withLabel(
                HAS_EFFECT_LABEL);
        builder.withProperty("a", Integer.parseInt(entry.a));
        builder.withProperty("b", Integer.parseInt(entry.b));
        builder.withProperty("c", Integer.parseInt(entry.c));
        builder.withProperty("d", Integer.parseInt(entry.d));
        builder.withProperty("prr", entry.prr);
        builder.withProperty("prr_error", entry.prrError);
        builder.withProperty("mean_reporting_frequency", entry.meanReportingFrequency);
        builder.build();
    }

    private long getOrCreateDrugNode(final Graph graph, final Map<String, Long> rxnormIdNodeIdMap,
                                     final String rxnormId, final String name) {
        Long nodeId = rxnormIdNodeIdMap.get(rxnormId);
        if (nodeId == null) {
            nodeId = graph.addNode(DRUG_LABEL, ID_KEY, rxnormId, "name", name).getId();
            rxnormIdNodeIdMap.put(rxnormId, nodeId);
        }
        return nodeId;
    }

    private long getOrCreateDrugEffectNode(final Graph graph, final Map<String, Long> meddraIdNodeIdMap,
                                           final String meddraId, final String name) {
        Long nodeId = meddraIdNodeIdMap.get(meddraId);
        if (nodeId == null) {
            nodeId = graph.addNode(DRUG_EFFECT_LABEL, ID_KEY, meddraId, "name", name).getId();
            meddraIdNodeIdMap.put(meddraId, nodeId);
        }
        return nodeId;
    }

    private void exportTwosidesEntries(final Workspace workspace, final Graph graph,
                                       final Map<String, Long> rxnormIdNodeIdMap,
                                       final Map<String, Long> meddraIdNodeIdMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting TWOSIDES...");
        graph.beginEdgeIndicesDelay(PART_OF_LABEL);
        graph.beginEdgeIndicesDelay(HAS_EFFECT_LABEL);
        final Map<Long, Map<Long, Long>> drugIdsPairIdMap = new HashMap<>();
        try (final var zipStream = FileUtils.openGzip(workspace, dataSource, NSIDESUpdater.TWOSIDES_FILE_NAME)) {
            final MappingIterator<TwosidesEntry> entries = FileUtils.openCsvWithHeader(zipStream, TwosidesEntry.class);
            while (entries.hasNext()) {
                exportTwosidesEntry(graph, entries.next(), rxnormIdNodeIdMap, meddraIdNodeIdMap, drugIdsPairIdMap);
            }
        } catch (IOException e) {
            throw new ExporterException(e);
        }
        graph.endEdgeIndicesDelay(PART_OF_LABEL);
        graph.endEdgeIndicesDelay(HAS_EFFECT_LABEL);
    }

    private void exportTwosidesEntry(final Graph graph, final TwosidesEntry entry,
                                     final Map<String, Long> rxnormIdNodeIdMap,
                                     final Map<String, Long> meddraIdNodeIdMap,
                                     final Map<Long, Map<Long, Long>> drugIdsPairIdMap) {
        if ("A".equals(entry.a))
            return;
        final long drug1NodeId = getOrCreateDrugNode(graph, rxnormIdNodeIdMap, entry.drug1RxnormId,
                                                     entry.drug1ConceptName);
        final long drug2NodeId = getOrCreateDrugNode(graph, rxnormIdNodeIdMap, entry.drug2RxnormId,
                                                     entry.drug2ConceptName);
        final Map<Long, Long> map = drugIdsPairIdMap.computeIfAbsent(Math.min(drug1NodeId, drug2NodeId),
                                                                     (id) -> new HashMap<>());
        final var maxDrugId = Math.max(drug1NodeId, drug2NodeId);
        Long drugCombinationNodeId = map.get(maxDrugId);
        if (drugCombinationNodeId == null) {
            drugCombinationNodeId = graph.addNode("DrugCombination").getId();
            map.put(maxDrugId, drugCombinationNodeId);
            graph.addEdge(drug1NodeId, drugCombinationNodeId, PART_OF_LABEL);
            graph.addEdge(drug2NodeId, drugCombinationNodeId, PART_OF_LABEL);
        }
        final long drugEffectNodeId = getOrCreateDrugEffectNode(graph, meddraIdNodeIdMap, entry.conditionMeddraId,
                                                                entry.conditionConceptName);
        final var builder = graph.buildEdge(HAS_EFFECT_LABEL).fromNode(drugCombinationNodeId).toNode(drugEffectNodeId);
        builder.withProperty("a", Integer.parseInt(entry.a));
        builder.withProperty("b", Integer.parseInt(entry.b));
        builder.withProperty("c", Integer.parseInt(entry.c));
        builder.withProperty("d", Integer.parseInt(entry.d));
        builder.withProperty("prr", entry.prr);
        builder.withProperty("prr_error", entry.prrError);
        builder.withProperty("mean_reporting_frequency", entry.meanReportingFrequency);
        builder.build();
    }
}
