package de.unibi.agbi.biodwh2.rnainter.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.rnainter.RNAInterDataSource;
import de.unibi.agbi.biodwh2.rnainter.model.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RNAInterGraphExporter extends GraphExporter<RNAInterDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(RNAInterGraphExporter.class);
    private static final String SKIP_ROW_KEYWORD1 = "Category1";
    private static final String SKIP_ROW_KEYWORD2 = "Category2";
    static final String COMPOUND_LABEL = "Compound";
    static final String GENE_LABEL = "Gene";
    static final String PROTEIN_LABEL = "Protein";
    private static final String HISTONE_MODIFICATION_LABEL = "HistoneModification";
    static final String RNA_LABEL = "RNA";
    public static final String INTERACTS_WITH_LABEL = "INTERACTS_WITH";
    public static final String NOT_AVAILABLE_VALUE = "N/A";
    private static final String RNA_BINDING_PROTEIN_TYPE = "RBP";
    private static final String TF_TYPE = "TF";
    private static final String PROTEIN_TYPE = "P";
    private static final String SYMBOL_KEY = "symbol";
    private static final String SPECIES_KEY = "species";
    static final String NAME_KEY = "name";
    static final String TYPE_KEY = "type";

    public RNAInterGraphExporter(final RNAInterDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(COMPOUND_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(RNA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.beginEdgeIndicesDelay(INTERACTS_WITH_LABEL);
        final Map<String, Map<String, Map<String, Long>>> rnaKeyNodeIdMap = new HashMap<>();
        exportRRInteractions(workspace, graph, rnaKeyNodeIdMap);
        exportRPInteractions(workspace, graph, rnaKeyNodeIdMap);
        exportRDInteractions(workspace, graph, rnaKeyNodeIdMap);
        exportRCInteractions(workspace, graph, rnaKeyNodeIdMap);
        exportRHInteractions(workspace, graph, rnaKeyNodeIdMap);
        graph.endEdgeIndicesDelay(INTERACTS_WITH_LABEL);
        return true;
    }

    private void exportRRInteractions(final Workspace workspace, final Graph graph,
                                      final Map<String, Map<String, Map<String, Long>>> rnaKeyNodeIdMap) {
        try (final MappingIterator<Entry> iterator = parseTsvFile(workspace, RNAInterUpdater.RR_FILE_NAME)) {
            while (iterator.hasNext()) {
                final Entry entry = iterator.next();
                // Skip strange header line within RR and RP file
                if (SKIP_ROW_KEYWORD1.equals(entry.category1) || SKIP_ROW_KEYWORD2.equals(entry.category2))
                    continue;
                final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                                   entry.species1, rnaKeyNodeIdMap);
                final Long toId = getOrCreateRNA(graph, entry.interactor2Symbol, entry.rawId2, entry.category2,
                                                 entry.species2, rnaKeyNodeIdMap);
                graph.addEdgeFromModel(fromId, toId, entry, ID_KEY, entry.rnaInterId);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export file '" + RNAInterUpdater.RR_FILE_NAME + "'", e);
        }
    }

    private MappingIterator<Entry> parseTsvFile(final Workspace workspace, final String fileName) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        return FileUtils.openTarGzipTsvWithHeader(workspace, dataSource, fileName, Entry.class);
    }

    private void exportRPInteractions(final Workspace workspace, final Graph graph,
                                      final Map<String, Map<String, Map<String, Long>>> rnaKeyNodeIdMap) {
        final Map<String, Map<String, Long>> proteinKeyNodeIdMap = new HashMap<>();
        try (final MappingIterator<Entry> iterator = parseTsvFile(workspace, RNAInterUpdater.RP_FILE_NAME)) {
            while (iterator.hasNext()) {
                final Entry entry = iterator.next();
                // Skip strange header line within RR and RP file
                if (SKIP_ROW_KEYWORD1.equals(entry.category1) || SKIP_ROW_KEYWORD2.equals(entry.category2))
                    continue;
                final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                                   entry.species1, rnaKeyNodeIdMap);
                final Long toId = getOrCreateProtein(graph, entry.interactor2Symbol, entry.rawId2, entry.category2,
                                                     entry.species2, proteinKeyNodeIdMap);
                graph.addEdgeFromModel(fromId, toId, entry, ID_KEY, entry.rnaInterId);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export file '" + RNAInterUpdater.RP_FILE_NAME + "'", e);
        }
        proteinKeyNodeIdMap.clear();
    }

    private void exportRDInteractions(final Workspace workspace, final Graph graph,
                                      final Map<String, Map<String, Map<String, Long>>> rnaKeyNodeIdMap) {
        final Map<String, Long> geneKeyNodeIdMap = new HashMap<>();
        try (final MappingIterator<Entry> iterator = parseTsvFile(workspace, RNAInterUpdater.RD_FILE_NAME)) {
            while (iterator.hasNext()) {
                final Entry entry = iterator.next();
                final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                                   entry.species1, rnaKeyNodeIdMap);
                // DNA always has "DNA" category
                final Long toId = getOrCreateGene(graph, entry.interactor2Symbol, entry.rawId2, entry.species2,
                                                  geneKeyNodeIdMap);
                graph.addEdgeFromModel(fromId, toId, entry, ID_KEY, entry.rnaInterId);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export file '" + RNAInterUpdater.RD_FILE_NAME + "'", e);
        }
        geneKeyNodeIdMap.clear();
    }

    private void exportRCInteractions(final Workspace workspace, final Graph graph,
                                      final Map<String, Map<String, Map<String, Long>>> rnaKeyNodeIdMap) {
        final Map<String, Long> compoundNameNodeIdMap = new HashMap<>();
        try (final MappingIterator<Entry> iterator = parseTsvFile(workspace, RNAInterUpdater.RC_FILE_NAME)) {
            while (iterator.hasNext()) {
                final Entry entry = iterator.next();
                final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                                   entry.species1, rnaKeyNodeIdMap);
                // Compound always has N/A species and "compound" category
                final Long toId = getOrCreateCompound(graph, entry.interactor2Symbol, entry.rawId2,
                                                      compoundNameNodeIdMap);
                graph.addEdgeFromModel(fromId, toId, entry, ID_KEY, entry.rnaInterId);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export file '" + RNAInterUpdater.RC_FILE_NAME + "'", e);
        }
        compoundNameNodeIdMap.clear();
    }

    private void exportRHInteractions(final Workspace workspace, final Graph graph,
                                      final Map<String, Map<String, Map<String, Long>>> rnaKeyNodeIdMap) {
        final Map<String, Long> histoneModificationSymbolNodeIdMap = new HashMap<>();
        try (final MappingIterator<Entry> iterator = parseTsvFile(workspace, RNAInterUpdater.RH_FILE_NAME)) {
            while (iterator.hasNext()) {
                final Entry entry = iterator.next();
                final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                                   entry.species1, rnaKeyNodeIdMap);
                // Histone modification always has N/A species, N/A rawId and "histone modification" category
                final Long toId = getOrCreateHistoneModification(graph, entry.interactor2Symbol,
                                                                 histoneModificationSymbolNodeIdMap);
                graph.addEdgeFromModel(fromId, toId, entry, ID_KEY, entry.rnaInterId);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export file '" + RNAInterUpdater.RH_FILE_NAME + "'", e);
        }
        histoneModificationSymbolNodeIdMap.clear();
    }

    private Long getOrCreateRNA(final Graph graph, final String symbol, final String rawId, final String category,
                                final String species, final Map<String, Map<String, Map<String, Long>>> keyNodeIdMap) {
        if (rawId == null || NOT_AVAILABLE_VALUE.equals(rawId)) {
            final Map<String, Map<String, Long>> categorySymbolNodeIdMap = keyNodeIdMap.computeIfAbsent(species,
                                                                                                        k -> new HashMap<>());
            final Map<String, Long> symbolNodeIdMap = categorySymbolNodeIdMap.computeIfAbsent(category,
                                                                                              k -> new HashMap<>());
            Long nodeId = symbolNodeIdMap.get(symbol);
            if (nodeId == null) {
                nodeId = graph.addNode(RNA_LABEL, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY, category).getId();
                symbolNodeIdMap.put(symbol, nodeId);
            }
            return nodeId;
        }
        Node node = graph.findNode(RNA_LABEL, ID_KEY, rawId);
        if (node == null)
            node = graph.addNode(RNA_LABEL, ID_KEY, rawId, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY,
                                 category);
        return node.getId();
    }

    private Long getOrCreateProtein(final Graph graph, final String symbol, final String rawId, final String category,
                                    final String species, final Map<String, Map<String, Long>> keyNodeIdMap) {
        final String type = !RNA_BINDING_PROTEIN_TYPE.equals(category) && !TF_TYPE.equals(category) ? PROTEIN_TYPE :
                            category;
        if (rawId == null || NOT_AVAILABLE_VALUE.equals(rawId)) {
            final Map<String, Long> symbolNodeIdMap = keyNodeIdMap.computeIfAbsent(species, k -> new HashMap<>());
            Long nodeId = symbolNodeIdMap.get(symbol);
            if (nodeId == null) {
                nodeId = graph.addNode(PROTEIN_LABEL, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY, type).getId();
                symbolNodeIdMap.put(symbol, nodeId);
            }
            return nodeId;
        }
        Node node = graph.findNode(PROTEIN_LABEL, ID_KEY, rawId);
        if (node == null)
            node = graph.addNode(PROTEIN_LABEL, ID_KEY, rawId, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY,
                                 type);
        return node.getId();
    }

    private Long getOrCreateGene(final Graph graph, final String symbol, final String rawId, final String species,
                                 final Map<String, Long> keyNodeIdMap) {
        if (rawId == null || NOT_AVAILABLE_VALUE.equals(rawId)) {
            final String key = symbol + "|" + species;
            Long nodeId = keyNodeIdMap.get(key);
            if (nodeId == null) {
                nodeId = graph.addNode(GENE_LABEL, SYMBOL_KEY, symbol, SPECIES_KEY, species).getId();
                keyNodeIdMap.put(key, nodeId);
            }
            return nodeId;
        }
        Node node = graph.findNode(GENE_LABEL, ID_KEY, rawId);
        if (node == null)
            node = graph.addNode(GENE_LABEL, ID_KEY, rawId, SYMBOL_KEY, symbol, SPECIES_KEY, species);
        return node.getId();
    }

    private Long getOrCreateCompound(final Graph graph, final String name, final String rawId,
                                     final Map<String, Long> nameNodeIdMap) {
        if (rawId != null && !NOT_AVAILABLE_VALUE.equals(rawId)) {
            Node node = graph.findNode(COMPOUND_LABEL, ID_KEY, rawId);
            if (node == null)
                node = graph.addNode(COMPOUND_LABEL, ID_KEY, rawId, NAME_KEY, name);
            return node.getId();
        }
        Long nodeId = nameNodeIdMap.get(name);
        if (nodeId == null) {
            nodeId = graph.addNode(COMPOUND_LABEL, NAME_KEY, name).getId();
            nameNodeIdMap.put(name, nodeId);
        }
        return nodeId;
    }

    private Long getOrCreateHistoneModification(final Graph graph, final String symbol,
                                                final Map<String, Long> symbolNodeIdMap) {
        Long nodeId = symbolNodeIdMap.get(symbol);
        if (nodeId == null) {
            nodeId = graph.addNode(HISTONE_MODIFICATION_LABEL, SYMBOL_KEY, symbol).getId();
            symbolNodeIdMap.put(symbol, nodeId);
        }
        return nodeId;
    }
}
