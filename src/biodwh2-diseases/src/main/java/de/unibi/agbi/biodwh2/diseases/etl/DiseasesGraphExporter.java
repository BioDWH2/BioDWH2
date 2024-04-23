package de.unibi.agbi.biodwh2.diseases.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.diseases.DiseasesDataSource;
import de.unibi.agbi.biodwh2.diseases.model.ExperimentEntry;
import de.unibi.agbi.biodwh2.diseases.model.KnowledgeEntry;
import de.unibi.agbi.biodwh2.diseases.model.TextMiningEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiseasesGraphExporter extends GraphExporter<DiseasesDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(DiseasesGraphExporter.class);
    static final String GENE_LABEL = "Gene";
    static final String DISEASE_LABEL = "Disease";
    static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";

    public DiseasesGraphExporter(final DiseasesDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, Long> geneIdNodeIdMap = exportGenes(workspace, graph);
        final Map<String, Long> diseasesIdNodeIdMap = exportDiseases(workspace, graph);
        exportEdges(workspace, graph, geneIdNodeIdMap, diseasesIdNodeIdMap);
        return true;
    }

    private Map<String, Long> exportGenes(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting genes...");
        final Map<String, String> genes = new HashMap<>();
        // parseTsvFile(workspace, IntegratedEntry.class, DiseasesUpdater.INTEGRATED_FULL_FILE_NAME,
        //              (entry) -> updateEntityEntry(genes, entry.geneIdentifier, entry.geneName));
        parseTsvFile(workspace, KnowledgeEntry.class, DiseasesUpdater.KNOWLEDGE_FILTERED_FILE_NAME,
                     (entry) -> updateEntityEntry(genes, entry.geneIdentifier, entry.geneName));
        parseTsvFile(workspace, ExperimentEntry.class, DiseasesUpdater.EXPERIMENTS_FILTERED_FILE_NAME,
                     (entry) -> updateEntityEntry(genes, entry.geneIdentifier, entry.geneName));
        parseTsvFile(workspace, TextMiningEntry.class, DiseasesUpdater.TEXT_MINING_FILTERED_FILE_NAME,
                     (entry) -> updateEntityEntry(genes, entry.geneIdentifier, entry.geneName));
        final Map<String, Long> geneIdNodeIdMap = new HashMap<>();
        for (final String id : genes.keySet()) {
            final String name = genes.get(id);
            final Node node;
            if (id.equals(name))
                node = graph.addNode(GENE_LABEL, ID_KEY, id);
            else
                node = graph.addNode(GENE_LABEL, ID_KEY, id, "name", name);
            geneIdNodeIdMap.put(id, node.getId());
        }
        return geneIdNodeIdMap;
    }

    private <T> void parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass, final String fileName,
                                  FileUtils.IOConsumer<T> consumer) throws ExporterException {
        try {
            FileUtils.openTsvWithHeader(workspace, dataSource, fileName, typeVariableClass, consumer);
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void updateEntityEntry(final Map<String, String> entries, final String id, final String name) {
        final String existingName = entries.get(id);
        entries.put(id, existingName == null ? name : existingName);
    }

    private Map<String, Long> exportDiseases(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting diseases...");
        final Map<String, String> diseases = new HashMap<>();
        // parseTsvFile(workspace, IntegratedEntry.class, DiseasesUpdater.INTEGRATED_FULL_FILE_NAME,
        //              (entry) -> updateEntityEntry(diseases, entry.diseaseIdentifier, entry.diseaseName));
        parseTsvFile(workspace, KnowledgeEntry.class, DiseasesUpdater.KNOWLEDGE_FILTERED_FILE_NAME,
                     (entry) -> updateEntityEntry(diseases, entry.diseaseIdentifier, entry.diseaseName));
        parseTsvFile(workspace, ExperimentEntry.class, DiseasesUpdater.EXPERIMENTS_FILTERED_FILE_NAME,
                     (entry) -> updateEntityEntry(diseases, entry.diseaseIdentifier, entry.diseaseName));
        parseTsvFile(workspace, TextMiningEntry.class, DiseasesUpdater.TEXT_MINING_FILTERED_FILE_NAME,
                     (entry) -> updateEntityEntry(diseases, entry.diseaseIdentifier, entry.diseaseName));
        final Map<String, Long> diseaseIdNodeIdMap = new HashMap<>();
        for (final String id : diseases.keySet()) {
            final String name = diseases.get(id);
            final Node node;
            if (id.equals(name))
                node = graph.addNode(DISEASE_LABEL, ID_KEY, id);
            else
                node = graph.addNode(DISEASE_LABEL, ID_KEY, id, "name", name);
            diseaseIdNodeIdMap.put(id, node.getId());
        }
        return diseaseIdNodeIdMap;
    }

    private void exportEdges(final Workspace workspace, final Graph graph, final Map<String, Long> geneIdNodeIdMap,
                             final Map<String, Long> diseasesIdNodeIdMap) {
        graph.beginEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
        // if (LOGGER.isInfoEnabled())
        //     LOGGER.info("Exporting " + DiseasesUpdater.INTEGRATED_FULL_FILE_NAME + "...");
        // parseTsvFile(workspace, IntegratedEntry.class, DiseasesUpdater.INTEGRATED_FULL_FILE_NAME,
        //              (entry) -> graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier),
        //                                       diseasesIdNodeIdMap.get(entry.diseaseIdentifier), ASSOCIATED_WITH_LABEL,
        //                                       "integrated_confidence_score", entry.confidenceScore));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + DiseasesUpdater.KNOWLEDGE_FILTERED_FILE_NAME + "...");
        parseTsvFile(workspace, KnowledgeEntry.class, DiseasesUpdater.KNOWLEDGE_FILTERED_FILE_NAME,
                     (entry) -> graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier),
                                              diseasesIdNodeIdMap.get(entry.diseaseIdentifier), ASSOCIATED_WITH_LABEL,
                                              "knowledge_confidence_score", entry.confidenceScore, "knowledge_source",
                                              entry.sourceDatabase, "knowledge_evidence_type", entry.evidenceType,
                                              "evidence_type", "data_source"));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + DiseasesUpdater.EXPERIMENTS_FILTERED_FILE_NAME + "...");
        parseTsvFile(workspace, ExperimentEntry.class, DiseasesUpdater.EXPERIMENTS_FILTERED_FILE_NAME,
                     (entry) -> graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier),
                                              diseasesIdNodeIdMap.get(entry.diseaseIdentifier), ASSOCIATED_WITH_LABEL,
                                              "experiment_confidence_score", entry.confidenceScore, "experiment_source",
                                              entry.sourceDatabase, "experiment_source_score", entry.sourceScore,
                                              "evidence_type", "experiment"));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + DiseasesUpdater.TEXT_MINING_FILTERED_FILE_NAME + "...");
        parseTsvFile(workspace, TextMiningEntry.class, DiseasesUpdater.TEXT_MINING_FILTERED_FILE_NAME,
                     (entry) -> graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier),
                                              diseasesIdNodeIdMap.get(entry.diseaseIdentifier), ASSOCIATED_WITH_LABEL,
                                              "textmining_confidence_score", entry.confidenceScore,
                                              "textmining_z_score", entry.zScore, "textmining_viewer_url",
                                              entry.viewerUrl, "evidence_type", "text_mining"));
        graph.endEdgeIndicesDelay(ASSOCIATED_WITH_LABEL);
    }
}
