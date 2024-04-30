package de.unibi.agbi.biodwh2.rnadisease.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.XlsxMappingIterator;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.rnadisease.RNADiseaseDataSource;
import de.unibi.agbi.biodwh2.rnadisease.model.ExperimentalEntry;
import de.unibi.agbi.biodwh2.rnadisease.model.PredictedEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RNADiseaseGraphExporter extends GraphExporter<RNADiseaseDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(RNADiseaseGraphExporter.class);
    static final String RNA_LABEL = "RNA";
    static final String DISEASE_LABEL = "Disease";
    static final String DO_ID_KEY = "do_id";
    static final String KEGG_ID_KEY = "kegg_id";
    static final String MESH_ID_KEY = "mesh_id";
    private static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";

    public RNADiseaseGraphExporter(final RNADiseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(RNA_LABEL, "symbol", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, DO_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, KEGG_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, MESH_ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, Long> diseaseNameNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        exportExperimentalEntries(workspace, graph, diseaseNameNodeIdMap);
        exportPredictedEntries(workspace, graph, RNADiseaseUpdater.MIRNA_PREDICTED_FILE_NAME, diseaseNameNodeIdMap);
        exportPredictedEntries(workspace, graph, RNADiseaseUpdater.LNCRNA_PREDICTED_FILE_NAME, diseaseNameNodeIdMap);
        exportPredictedEntries(workspace, graph, RNADiseaseUpdater.CIRCRNA_PREDICTED_FILE_NAME, diseaseNameNodeIdMap);
        exportPredictedEntries(workspace, graph, RNADiseaseUpdater.PIRNA_PREDICTED_FILE_NAME, diseaseNameNodeIdMap);
        graph.endEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        return true;
    }

    private void exportExperimentalEntries(final Workspace workspace, final Graph graph,
                                           final Map<String, Long> diseaseNameNodeIdMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting '{}'...", RNADiseaseUpdater.ALL_EXPERIMENTAL_FILE_NAME);
        try (final XlsxMappingIterator<ExperimentalEntry> iterator = openExperimentalZipXlsx(workspace)) {
            while (iterator.hasNext())
                exportExperimentalEntry(graph, diseaseNameNodeIdMap, iterator.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private XlsxMappingIterator<ExperimentalEntry> openExperimentalZipXlsx(final Workspace workspace) {
        try {
            final ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource,
                                                                    RNADiseaseUpdater.ALL_EXPERIMENTAL_FILE_NAME);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.getName().startsWith("._") && zipEntry.getName().endsWith(".xlsx")) {
                    return new XlsxMappingIterator<>(ExperimentalEntry.class, zipInputStream);
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(
                    "Failed to parse the file '" + RNADiseaseUpdater.ALL_EXPERIMENTAL_FILE_NAME + "'", e);
        }
        throw new ExporterException(
                "Failed to parse the missing file '" + RNADiseaseUpdater.ALL_EXPERIMENTAL_FILE_NAME + "'");
    }

    private void exportExperimentalEntry(final Graph graph, final Map<String, Long> diseaseNameNodeIdMap,
                                         final ExperimentalEntry entry) {
        final Node rnaNode = getOrCreateRNANode(graph, entry.rnaSymbol, entry.rnaType, entry.species);
        final Node diseaseNode = getOrCreateDiseaseNode(graph, entry.diseaseName, entry.doId, entry.keggDiseaseId,
                                                        entry.meshId, diseaseNameNodeIdMap);
        graph.addEdge(rnaNode, diseaseNode, ASSOCIATED_WITH_LABEL, ID_KEY, entry.rdId, "pmid", entry.pmid, "score",
                      entry.score);
    }

    private Node getOrCreateRNANode(final Graph graph, final String symbol, final String type, final String species) {
        Node node = graph.findNode(RNA_LABEL, "symbol", symbol);
        if (node == null) {
            if (species == null)
                node = graph.addNode(RNA_LABEL, "symbol", symbol, "type", type);
            else
                node = graph.addNode(RNA_LABEL, "symbol", symbol, "type", type, "species", species);
        }
        return node;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final String name, final String doId, final String keggId,
                                        final String meshId, final Map<String, Long> diseaseNameNodeIdMap) {
        Integer doIdNumber = null;
        if (StringUtils.isNotEmpty(doId))
            doIdNumber = Integer.parseInt(StringUtils.replace(doId, "DOID:", "").strip());
        Node node = null;
        if (doIdNumber != null)
            node = graph.findNode(DISEASE_LABEL, DO_ID_KEY, doIdNumber);
        if (node == null && keggId != null)
            node = graph.findNode(DISEASE_LABEL, KEGG_ID_KEY, keggId);
        if (node == null && meshId != null)
            node = graph.findNode(DISEASE_LABEL, MESH_ID_KEY, meshId);
        if (node == null) {
            final Long nodeId = diseaseNameNodeIdMap.get(name);
            if (nodeId != null)
                node = graph.getNode(nodeId);
        }
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(DISEASE_LABEL);
            builder.withPropertyIfNotNull("name", name);
            builder.withPropertyIfNotNull(DO_ID_KEY, doIdNumber);
            builder.withPropertyIfNotNull(KEGG_ID_KEY, keggId);
            builder.withPropertyIfNotNull(MESH_ID_KEY, meshId);
            node = builder.build();
            diseaseNameNodeIdMap.put(name, node.getId());
        }
        return node;
    }

    private void exportPredictedEntries(final Workspace workspace, final Graph graph, final String fileName,
                                        final Map<String, Long> diseaseNameNodeIdMap) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting '{}'...", fileName);
        int ignoredLinesCount = 0;
        try (final MappingIterator<PredictedEntry> iterator = openPredictedZipCsv(workspace, fileName)) {
            while (iterator.hasNext()) {
                try {
                    exportPredictedEntry(graph, diseaseNameNodeIdMap, iterator.next());
                } catch (RuntimeJsonMappingException e) {
                    if (e.getCause() instanceof CsvReadException)
                        ignoredLinesCount++;
                    else
                        throw e;
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        if (ignoredLinesCount > 0 && LOGGER.isWarnEnabled())
            LOGGER.warn("Ignored {} malformed csv lines", ignoredLinesCount);
    }

    private MappingIterator<PredictedEntry> openPredictedZipCsv(final Workspace workspace, final String fileName) {
        try {
            final ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource, fileName);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.getName().startsWith("._") && zipEntry.getName().endsWith(".csv")) {
                    return FileUtils.openSeparatedValuesFile(zipInputStream, PredictedEntry.class, ',', true);
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
        throw new ExporterException("Failed to parse the missing file '" + fileName + "'");
    }

    private void exportPredictedEntry(final Graph graph, final Map<String, Long> diseaseNameNodeIdMap,
                                      final PredictedEntry entry) {
        final Node rnaNode = getOrCreateRNANode(graph, entry.rnaSymbol, entry.rnaType, null);
        final Node diseaseNode = getOrCreateDiseaseNode(graph, entry.diseaseName, null, null, null,
                                                        diseaseNameNodeIdMap);
        graph.addEdge(rnaNode, diseaseNode, ASSOCIATED_WITH_LABEL, ID_KEY, entry.rdId, "rd_score", entry.rdScore,
                      "method", entry.methodName, "algorithm_score", entry.algorithmScore);
    }
}
