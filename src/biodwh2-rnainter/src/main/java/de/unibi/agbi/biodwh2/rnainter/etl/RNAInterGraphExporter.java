package de.unibi.agbi.biodwh2.rnainter.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.rnainter.RNAInterDataSource;
import de.unibi.agbi.biodwh2.rnainter.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RNAInterGraphExporter extends GraphExporter<RNAInterDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RNAInterGraphExporter.class);
    private static final String SKIP_ROW_KEYWORD1 = "Category1";
    private static final String SKIP_ROW_KEYWORD2 = "Category2";
    private static final String COMPOUND_LABEL = "Compound";
    private static final String GENE_LABEL = "Gene";
    private static final String PROTEIN_LABEL = "Protein";
    public static final String HISTONE_MODIFICATION_LABEL = "HistoneModification";
    public static final String RNA_LABEL = "RNA";
    private static final String INTERACTS_WITH_LABEL = "INTERACTS_WITH";
    private static final String NOT_AVAILABLE_VALUE = "N/A";
    public static final String SYMBOL_KEY = "symbol";
    public static final String SPECIES_KEY = "species";
    public static final String NAME_KEY = "name";
    public static final String TYPE_KEY = "type";
    public static final String RNA_BINDING_PROTEIN_TYPE = "RBP";
    public static final String TF_TYPE = "TF";
    public static final String PROTEIN_TYPE = "P";

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
        final Map<String, Long> rnaIdNodeIdMap = new HashMap<>();
        final Map<String, Long> rnaKeyNodeIdMap = new HashMap<>();
        for (final Entry entry : parseTsvFile(workspace, RNAInterUpdater.RR_FILE_NAME)) {
            // Skip strange header line within RR and RP file
            if (SKIP_ROW_KEYWORD1.equals(entry.category1) || SKIP_ROW_KEYWORD2.equals(entry.category2))
                continue;
            final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                               entry.species1, rnaIdNodeIdMap, rnaKeyNodeIdMap);
            final Long toId = getOrCreateRNA(graph, entry.interactor2Symbol, entry.rawId2, entry.category2,
                                             entry.species2, rnaIdNodeIdMap, rnaKeyNodeIdMap);
            createEdge(graph, fromId, toId, entry);
        }
        final Map<String, Long> proteinIdNodeIdMap = new HashMap<>();
        final Map<String, Long> proteinKeyNodeIdMap = new HashMap<>();
        for (final Entry entry : parseTsvFile(workspace, RNAInterUpdater.RP_FILE_NAME)) {
            // Skip strange header line within RR and RP file
            if (SKIP_ROW_KEYWORD1.equals(entry.category1) || SKIP_ROW_KEYWORD2.equals(entry.category2))
                continue;
            final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                               entry.species1, rnaIdNodeIdMap, rnaKeyNodeIdMap);
            final Long toId = getOrCreateProtein(graph, entry.interactor2Symbol, entry.rawId2, entry.category2,
                                                 entry.species2, proteinIdNodeIdMap, proteinKeyNodeIdMap);
            createEdge(graph, fromId, toId, entry);
        }
        proteinIdNodeIdMap.clear();
        proteinKeyNodeIdMap.clear();
        final Map<String, Long> geneKeyNodeIdMap = new HashMap<>();
        for (final Entry entry : parseTsvFile(workspace, RNAInterUpdater.RD_FILE_NAME)) {
            final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                               entry.species1, rnaIdNodeIdMap, rnaKeyNodeIdMap);
            // DNA always has "DNA" category
            final Long toId = getOrCreateGene(graph, entry.interactor2Symbol, entry.rawId2, entry.species2,
                                              geneKeyNodeIdMap);
            createEdge(graph, fromId, toId, entry);
        }
        geneKeyNodeIdMap.clear();
        final Map<String, Long> compoundNameNodeIdMap = new HashMap<>();
        for (final Entry entry : parseTsvFile(workspace, RNAInterUpdater.RC_FILE_NAME)) {
            final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                               entry.species1, rnaIdNodeIdMap, rnaKeyNodeIdMap);
            // Compound always has N/A species and "compound" category
            final Long toId = getOrCreateCompound(graph, entry.interactor2Symbol, entry.rawId2, compoundNameNodeIdMap);
            createEdge(graph, fromId, toId, entry);
        }
        compoundNameNodeIdMap.clear();
        final Map<String, Long> histoneModificationSymbolNodeIdMap = new HashMap<>();
        for (final Entry entry : parseTsvFile(workspace, RNAInterUpdater.RH_FILE_NAME)) {
            final Long fromId = getOrCreateRNA(graph, entry.interactor1Symbol, entry.rawId1, entry.category1,
                                               entry.species1, rnaIdNodeIdMap, rnaKeyNodeIdMap);
            // Histone modification always has N/A species, N/A rawId and "histone modification" category
            final Long toId = getOrCreateHistoneModification(graph, entry.interactor2Symbol,
                                                             histoneModificationSymbolNodeIdMap);
            createEdge(graph, fromId, toId, entry);
        }
        histoneModificationSymbolNodeIdMap.clear();
        graph.endEdgeIndicesDelay(INTERACTS_WITH_LABEL);
        return true;
    }

    private Iterable<Entry> parseTsvFile(final Workspace workspace, final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            //noinspection resource
            final MappingIterator<Entry> iterator = FileUtils.openTarGzipTsvWithHeader(workspace, dataSource, fileName,
                                                                                       Entry.class);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private Long getOrCreateRNA(final Graph graph, final String symbol, final String rawId, final String category,
                                final String species, final Map<String, Long> idNodeIdMap,
                                final Map<String, Long> keyNodeIdMap) {
        /*
        mRNA    -   messenger RNA
        rRNA    -   ribosomal RNA
        tRNA    -   transfer RNA
        lncRNA  -   long non-coding RNA
        miRNA   -   micro RNA
        snRNA   -   small nuclear RNA
        snoRNA  -   small necleolar RNA
        eRNA    -   enhancer RNA
        vtRNAs  -   vault RNA
        ncRNA   -   non-coding RNA
        sncRNA  -   small non-coding RNA
        circRNA -   circular RNA
        lincRNA -   long intergenic non-coding RNA
        piRNA   -   PIWI-interacting RNA
        scaRNA  -   small cajal body-specific RNA
        scRNA   -   small conditional RNA
        shRNA   -   short hairpin RNA
        sRNA    -   bacterial small RNA
        misc_RNA
        miscRNA
        Mt_tRNA
        antisense
        non_stop_decay
        nonsense_mediated_decay
        PCG
        processed_transcript
        pseudo
        repeats
        retained_intron
        ribozyme
        TEC
        TR_C_gene
        tRF
        unassigned RNA
        unknown
        others
        */
        if (rawId == null || NOT_AVAILABLE_VALUE.equals(rawId)) {
            final String key = symbol + "|" + species + "|" + category;
            Long nodeId = keyNodeIdMap.get(key);
            if (nodeId == null) {
                nodeId = graph.addNode(RNA_LABEL, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY, category).getId();
                keyNodeIdMap.put(key, nodeId);
            }
            return nodeId;
        }
        Long nodeId = idNodeIdMap.get(rawId);
        if (nodeId == null) {
            nodeId = graph.addNode(RNA_LABEL, ID_KEY, rawId, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY,
                                   category).getId();
            idNodeIdMap.put(rawId, nodeId);
        }
        return nodeId;
    }

    private void createEdge(final Graph graph, final Long from, final Long to, final Entry entry) {
        final EdgeBuilder builder = graph.buildEdge().withLabel(INTERACTS_WITH_LABEL).fromNode(from).toNode(to);
        builder.withProperty("id", entry.rnaInterId);
        if (!NOT_AVAILABLE_VALUE.equals(entry.score))
            builder.withPropertyIfNotNull("score", entry.score);
        if (!NOT_AVAILABLE_VALUE.equals(entry.strong))
            builder.withPropertyIfNotNull("strong", entry.strong);
        if (!NOT_AVAILABLE_VALUE.equals(entry.weak))
            builder.withPropertyIfNotNull("weak", entry.weak);
        if (!NOT_AVAILABLE_VALUE.equals(entry.predict))
            builder.withPropertyIfNotNull("predict", entry.predict);
        builder.build();
    }

    private Long getOrCreateProtein(final Graph graph, final String symbol, final String rawId, final String category,
                                    final String species, final Map<String, Long> idNodeIdMap,
                                    final Map<String, Long> keyNodeIdMap) {
        final String type = !RNA_BINDING_PROTEIN_TYPE.equals(category) && !TF_TYPE.equals(category) ? PROTEIN_TYPE :
                            category;
        if (rawId == null || NOT_AVAILABLE_VALUE.equals(rawId)) {
            final String key = symbol + "|" + species;
            Long nodeId = keyNodeIdMap.get(key);
            if (nodeId == null) {
                nodeId = graph.addNode(PROTEIN_LABEL, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY, type).getId();
                keyNodeIdMap.put(key, nodeId);
            }
            return nodeId;
        }
        Long nodeId = idNodeIdMap.get(rawId);
        if (nodeId == null) {
            nodeId = graph.addNode(PROTEIN_LABEL, ID_KEY, rawId, SPECIES_KEY, species, SYMBOL_KEY, symbol, TYPE_KEY,
                                   type).getId();
            idNodeIdMap.put(rawId, nodeId);
        }
        return nodeId;
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
