package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import de.unibi.agbi.biodwh2.unii.model.UNIIDataEntry;
import de.unibi.agbi.biodwh2.unii.model.UNIIEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class UNIIGraphExporter extends GraphExporter<UNIIDataSource> {
    static final String UNII_LABEL = "UNII";
    static final String SPECIES_LABEL = "Species";
    static final String ITIS_IDS_KEY = "itis_ids";
    static final String NCBI_TAXONOMY_IDS_KEY = "ncbi_taxonomy_ids";
    static final String USDA_PLANTS_SYMBOLS_KEY = "usda_plants_symbols";
    private Map<Long, Long> itisIdNodeIdMap;
    private Map<Long, Long> ncbiTaxonomyIdNodeIdMap;
    private Map<String, Long> usdaPlantsSymbolNodeIdMap;

    public UNIIGraphExporter(final UNIIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        itisIdNodeIdMap = new HashMap<>();
        ncbiTaxonomyIdNodeIdMap = new HashMap<>();
        usdaPlantsSymbolNodeIdMap = new HashMap<>();
        graph.addIndex(IndexDescription.forNode(UNII_LABEL, "id", IndexDescription.Type.UNIQUE));
        Map<String, List<UNIIEntry>> uniiEntriesMap = new HashMap<>();
        for (UNIIEntry entry : dataSource.uniiEntries) {
            if (!uniiEntriesMap.containsKey(entry.unii))
                uniiEntriesMap.put(entry.unii, new ArrayList<>());
            uniiEntriesMap.get(entry.unii).add(entry);
        }
        for (Map.Entry<String, List<UNIIEntry>> entry : uniiEntriesMap.entrySet())
            createUNIINode(graph, entry.getValue(), dataSource.uniiDataEntries.get(entry.getKey()));
        return true;
    }

    private void createUNIINode(final Graph graph, final List<UNIIEntry> entries, final UNIIDataEntry dataEntry) {
        Node uniiNode;
        if (dataEntry == null)
            uniiNode = graph.addNode(UNII_LABEL, "id", entries.get(0).unii);
        else
            uniiNode = graph.addNodeFromModel(dataEntry);
        uniiNode.setProperty("name", entries.get(0).displayName);
        uniiNode.setProperty("official_names", getNameArrayOfTypeFromEntries(entries, "of"));
        uniiNode.setProperty("systematic_names", getNameArrayOfTypeFromEntries(entries, "sys"));
        uniiNode.setProperty("common_names", getNameArrayOfTypeFromEntries(entries, "cn"));
        uniiNode.setProperty("codes", getNameArrayOfTypeFromEntries(entries, "cd"));
        uniiNode.setProperty("brand_names", getNameArrayOfTypeFromEntries(entries, "bn"));
        graph.update(uniiNode);
        if (dataEntry.itis != null || dataEntry.ncbi != null || dataEntry.plants != null) {
            final Node speciesNode = getOrCreateTaxonomyNode(graph, dataEntry);
            graph.addEdge(uniiNode, speciesNode, "PART_OF_SPECIES");
        }
    }

    private static String[] getNameArrayOfTypeFromEntries(final List<UNIIEntry> entries, final String type) {
        return entries.stream().filter(e -> e.type.equals(type)).map(e -> StringUtils.strip(e.name, "\\")).toArray(
                String[]::new);
    }

    private Node getOrCreateTaxonomyNode(final Graph graph, final UNIIDataEntry dataEntry) {
        final Set<Long> matchedNodeIds = new HashSet<>();
        if (dataEntry.itis != null && itisIdNodeIdMap.containsKey(dataEntry.itis))
            matchedNodeIds.add(itisIdNodeIdMap.get(dataEntry.itis));
        if (dataEntry.ncbi != null && ncbiTaxonomyIdNodeIdMap.containsKey(dataEntry.ncbi))
            matchedNodeIds.add(ncbiTaxonomyIdNodeIdMap.get(dataEntry.ncbi));
        if (dataEntry.plants != null && usdaPlantsSymbolNodeIdMap.containsKey(dataEntry.plants))
            matchedNodeIds.add(usdaPlantsSymbolNodeIdMap.get(dataEntry.plants));
        final Set<Long> itisIds = new HashSet<>();
        if (dataEntry.itis != null)
            itisIds.add(dataEntry.itis);
        final Set<Long> ncbiTaxonomyIds = new HashSet<>();
        if (dataEntry.ncbi != null)
            ncbiTaxonomyIds.add(dataEntry.ncbi);
        final Set<String> usdaPlantsSymbols = new HashSet<>();
        if (dataEntry.plants != null)
            usdaPlantsSymbols.add(dataEntry.plants);
        Node node;
        if (matchedNodeIds.size() == 0) {
            node = graph.addNode(SPECIES_LABEL, ITIS_IDS_KEY, itisIds, NCBI_TAXONOMY_IDS_KEY, ncbiTaxonomyIds,
                                 USDA_PLANTS_SYMBOLS_KEY, usdaPlantsSymbols);
        } else {
            final Long[] matchedNodeIdsArray = matchedNodeIds.toArray(new Long[0]);
            node = graph.getNode(matchedNodeIdsArray[0]);
            final int itisIdsSize = itisIds.size();
            final int ncbiTaxonomyIdsSize = ncbiTaxonomyIds.size();
            final int usdaPlantsSymbolsSize = usdaPlantsSymbols.size();
            itisIds.addAll(node.<Set<Long>>getProperty(ITIS_IDS_KEY));
            ncbiTaxonomyIds.addAll(node.<Set<Long>>getProperty(NCBI_TAXONOMY_IDS_KEY));
            usdaPlantsSymbols.addAll(node.<Set<String>>getProperty(USDA_PLANTS_SYMBOLS_KEY));
            for (int i = 1; i < matchedNodeIdsArray.length; i++) {
                final Node nodeToMerge = graph.getNode(matchedNodeIdsArray[i]);
                itisIds.addAll(nodeToMerge.<Set<Long>>getProperty(ITIS_IDS_KEY));
                ncbiTaxonomyIds.addAll(nodeToMerge.<Set<Long>>getProperty(NCBI_TAXONOMY_IDS_KEY));
                usdaPlantsSymbols.addAll(nodeToMerge.<Set<String>>getProperty(USDA_PLANTS_SYMBOLS_KEY));
                graph.mergeNodes(node, nodeToMerge);
            }
            if (itisIdsSize != itisIds.size() || ncbiTaxonomyIdsSize != ncbiTaxonomyIds.size() ||
                usdaPlantsSymbolsSize != usdaPlantsSymbols.size()) {
                node.setProperty(ITIS_IDS_KEY, itisIds);
                node.setProperty(NCBI_TAXONOMY_IDS_KEY, ncbiTaxonomyIds);
                node.setProperty(USDA_PLANTS_SYMBOLS_KEY, usdaPlantsSymbols);
                graph.update(node);
            }
        }
        for (final Long itisId : itisIds)
            itisIdNodeIdMap.put(itisId, node.getId());
        for (final Long ncbiTaxonomyId : ncbiTaxonomyIds)
            ncbiTaxonomyIdNodeIdMap.put(ncbiTaxonomyId, node.getId());
        for (final String usdaPlantsSymbol : usdaPlantsSymbols)
            usdaPlantsSymbolNodeIdMap.put(usdaPlantsSymbol, node.getId());
        return node;
    }
}
