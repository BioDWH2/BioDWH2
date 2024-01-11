package de.unibi.agbi.biodwh2.tatonetti2012.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.tatonetti2012.Tatonetti2012DataSource;
import de.unibi.agbi.biodwh2.tatonetti2012.model.OffsidesEntry;
import de.unibi.agbi.biodwh2.tatonetti2012.model.TwosidesEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Tatonetti2012GraphExporter extends GraphExporter<Tatonetti2012DataSource> {
    private static final Logger LOGGER = LogManager.getLogger(Tatonetti2012GraphExporter.class);

    public Tatonetti2012GraphExporter(final Tatonetti2012DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Drug", ID_KEY, false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("DrugEffect", ID_KEY, false, IndexDescription.Type.UNIQUE));
        final Map<String, Long> stitchIdNodeIdMap = new HashMap<>();
        final Map<String, Long> umlsIdNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay("ASSOCIATED_WITH");
        graph.beginEdgeIndicesDelay("HAS_EFFECT");
        exportOffsidesEntries(workspace, graph, stitchIdNodeIdMap, umlsIdNodeIdMap);
        exportTwosidesEntries(workspace, graph, stitchIdNodeIdMap, umlsIdNodeIdMap);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Updating indices...");
        graph.endEdgeIndicesDelay("ASSOCIATED_WITH");
        graph.endEdgeIndicesDelay("HAS_EFFECT");
        return true;
    }

    private void exportOffsidesEntries(final Workspace workspace, final Graph graph,
                                       final Map<String, Long> stitchIdNodeIdMap,
                                       final Map<String, Long> umlsIdNodeIdMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting OFFSIDES...");
        try (final ZipInputStream zipStream = FileUtils.openZip(workspace, dataSource,
                                                                Tatonetti2012Updater.OFFSIDES_FILE_NAME)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".tsv")) {
                    final MappingIterator<OffsidesEntry> entries = FileUtils.openTsvWithHeader(zipStream,
                                                                                               OffsidesEntry.class);
                    while (entries.hasNext())
                        exportOffsidesEntry(graph, entries.next(), stitchIdNodeIdMap, umlsIdNodeIdMap);
                }
            }
        } catch (IOException e) {
            throw new ExporterException(e);
        }
    }

    private void exportOffsidesEntry(final Graph graph, final OffsidesEntry entry,
                                     final Map<String, Long> stitchIdNodeIdMap,
                                     final Map<String, Long> umlsIdNodeIdMap) {
        final long drugNodeId = getOrCreateDrugNode(graph, stitchIdNodeIdMap, entry.stitchId, entry.drug);
        final long drugEffectNodeId = getOrCreateDrugEffectNode(graph, umlsIdNodeIdMap, entry.eventUmlsId,
                                                                entry.eventName);
        final EdgeBuilder builder = graph.buildEdge().fromNode(drugNodeId).toNode(drugEffectNodeId).withLabel(
                "HAS_EFFECT");
        builder.withProperty("rr", entry.rr);
        builder.withProperty("log2rr", entry.log2rr);
        builder.withProperty("t_statistic", entry.tStatistic);
        builder.withProperty("pvalue", entry.pValue);
        builder.withProperty("observed", entry.observed);
        builder.withProperty("expected", entry.expected);
        builder.withProperty("bg_correction", entry.bgCorrection);
        builder.withProperty("sider", entry.sider);
        builder.withProperty("future_aers", entry.futureAers);
        builder.withProperty("medeffect", entry.medEffect);
        builder.build();
    }

    private long getOrCreateDrugNode(final Graph graph, final Map<String, Long> stitchIdNodeIdMap,
                                     final String stitchId, final String name) {
        Long nodeId = stitchIdNodeIdMap.get(stitchId);
        if (nodeId == null) {
            nodeId = graph.addNode("Drug", ID_KEY, stitchId, "name", name).getId();
            stitchIdNodeIdMap.put(stitchId, nodeId);
        }
        return nodeId;
    }

    private long getOrCreateDrugEffectNode(final Graph graph, final Map<String, Long> umlsIdNodeIdMap,
                                           final String umlsId, final String name) {
        Long nodeId = umlsIdNodeIdMap.get(umlsId);
        if (nodeId == null) {
            nodeId = graph.addNode("DrugEffect", ID_KEY, umlsId, "name", name).getId();
            umlsIdNodeIdMap.put(umlsId, nodeId);
        }
        return nodeId;
    }

    private void exportTwosidesEntries(final Workspace workspace, final Graph graph,
                                       final Map<String, Long> stitchIdNodeIdMap,
                                       final Map<String, Long> umlsIdNodeIdMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting TWOSIDES...");
        try (final ZipInputStream zipStream = FileUtils.openZip(workspace, dataSource,
                                                                Tatonetti2012Updater.TWOSIDES_FILE_NAME)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".tsv")) {
                    final MappingIterator<TwosidesEntry> entries = FileUtils.openTsvWithHeader(zipStream,
                                                                                               TwosidesEntry.class);
                    while (entries.hasNext())
                        exportTwosidesEntry(graph, entries.next(), stitchIdNodeIdMap, umlsIdNodeIdMap);
                }
            }
        } catch (IOException e) {
            throw new ExporterException(e);
        }
    }

    private void exportTwosidesEntry(final Graph graph, final TwosidesEntry entry,
                                     final Map<String, Long> stitchIdNodeIdMap,
                                     final Map<String, Long> umlsIdNodeIdMap) {
        final long drug1NodeId = getOrCreateDrugNode(graph, stitchIdNodeIdMap, entry.stitchId1, entry.drug1);
        final long drug2NodeId = getOrCreateDrugNode(graph, stitchIdNodeIdMap, entry.stitchId2, entry.drug2);
        final long drugEffectNodeId = getOrCreateDrugEffectNode(graph, umlsIdNodeIdMap, entry.eventUmlsId,
                                                                entry.eventName);
        final NodeBuilder builder = graph.buildNode().withLabel("TwosidesAssociation");
        builder.withProperty("proportional_reporting_ratio", entry.proportionalReportingRatio);
        builder.withProperty("pvalue", entry.pValue);
        builder.withProperty("confidence", entry.confidence);
        if (!"\\N".equals(entry.drug1Prr))
            builder.withProperty("drug1_prr", entry.drug1Prr);
        if (!"\\N".equals(entry.drug2Prr))
            builder.withProperty("drug2_prr", entry.drug2Prr);
        builder.withProperty("observed", entry.observed);
        builder.withProperty("expected", entry.expected);
        final Node associationNode = builder.build();
        graph.addEdge(drug1NodeId, associationNode, "ASSOCIATED_WITH");
        graph.addEdge(drug2NodeId, associationNode, "ASSOCIATED_WITH");
        graph.addEdge(associationNode, drugEffectNodeId, "HAS_EFFECT");
    }
}
