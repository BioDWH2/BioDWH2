package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import de.unibi.agbi.biodwh2.itis.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ITISGraphExporter extends GraphExporter<ITISDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITISGraphExporter.class);
    static final String GEOGRAPHIC_DIVISION_LABEL = "GeographicDivision";
    static final String JURISDICTION_LABEL = "Jurisdiction";
    static final String EXPERT_LABEL = "Expert";
    static final String KINGDOM_LABEL = "Kingdom";
    static final String RANK_LABEL = "Rank";
    static final String TAXON_LABEL = "Taxon";
    static final String COMMENT_LABEL = "Comment";
    static final String PUBLICATION_LABEL = "Publication";
    static final String SOURCE_LABEL = "Source";

    public ITISGraphExporter(final ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys(ID_KEY);
        LOGGER.info("Exporting comments...");
        createNodesFromModels(graph, dataSource.comments);
        LOGGER.info("Exporting experts...");
        createNodesFromModels(graph, dataSource.experts);
        LOGGER.info("Exporting sources...");
        createNodesFromModels(graph, dataSource.otherSources);
        LOGGER.info("Exporting publications...");
        createNodesFromModels(graph, dataSource.publications);
        LOGGER.info("Exporting kingdoms...");
        createNodesFromModels(graph, dataSource.kingdoms);
        LOGGER.info("Exporting taxon authors...");
        createTaxonAuthorNodes(graph);
        LOGGER.info("Exporting ranks...");
        createTaxonUnitTypeNodes(graph);
        LOGGER.info("Exporting taxonomic units...");
        final Map<Integer, Long> taxonTsnNodeIdMap = createTaxonomicUnitNodes(graph);
        LOGGER.info("Exporting taxonomic unit reference links...");
        createReferenceEdges(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting taxonomic unit hierarchy...");
        createHierarchyEdges(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting taxonomic unit comment links...");
        createTaxonomicUnitCommentEdges(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting taxonomic unit synonym links...");
        createTaxonomicUnitSynonymEdges(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting geographic divisions...");
        createGeographicDivisionNodes(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting jurisdictions...");
        createJurisdictionNodes(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting vernaculars...");
        Map<Integer, Long> vernacularIdNodeIdMap = createVernacularNodes(graph, taxonTsnNodeIdMap);
        taxonTsnNodeIdMap.clear();
        LOGGER.info("Exporting vernacular reference links...");
        createVernacularReferenceEdges(graph, vernacularIdNodeIdMap);
        return true;
    }

    private void createTaxonAuthorNodes(final Graph graph) {
        for (final TaxonAuthorLkp author : dataSource.taxonAuthorsLkps) {
            Node node = graph.addNodeFromModel(author);
            graph.addEdge(node, graph.findNode(KINGDOM_LABEL, ID_KEY, author.kingdomId), "ASSOCIATED_WITH");
        }
    }

    private void createTaxonUnitTypeNodes(final Graph graph) {
        final Map<String, Long> rankNodeIdLookup = new HashMap<>();
        for (final TaxonUnitType rank : dataSource.taxonUnitTypes) {
            final String id = rank.kingdomId + "_" + rank.id;
            final Node node = graph.addNode(RANK_LABEL, ID_KEY, id, "rank_id", rank.id, "name", rank.name);
            rankNodeIdLookup.put(id, node.getId());
            graph.addEdge(graph.findNode(KINGDOM_LABEL, ID_KEY, rank.kingdomId), node, "HAS_RANK");
        }
        for (final TaxonUnitType rank : dataSource.taxonUnitTypes) {
            final String id = rank.kingdomId + "_" + rank.id;
            final long nodeId = rankNodeIdLookup.get(id);
            graph.addEdge(nodeId, rankNodeIdLookup.get(rank.kingdomId + "_" + rank.dirParentRankId), "HAS_PARENT");
            graph.addEdge(nodeId, rankNodeIdLookup.get(rank.kingdomId + "_" + rank.reqParentRankId), "HAS_REQ_PARENT");
        }
    }

    private Map<Integer, Long> createTaxonomicUnitNodes(final Graph graph) {
        final Map<Integer, Long> taxonTsnNodeIdMap = new HashMap<>();
        for (final TaxonomicUnit taxon : dataSource.taxonomicUnits) {
            final String longName = dataSource.longNames.get(taxon.tsn);
            final String nodcId = dataSource.nodcIds.get(taxon.tsn);
            Node node;
            if (longName != null && nodcId != null)
                node = graph.addNodeFromModel(taxon, "long_name", longName, "nodc_id", nodcId);
            else if (longName != null)
                node = graph.addNodeFromModel(taxon, "long_name", longName);
            else if (nodcId != null)
                node = graph.addNodeFromModel(taxon, "nodc_id", nodcId);
            else
                node = graph.addNodeFromModel(taxon);
            taxonTsnNodeIdMap.put(taxon.tsn, node.getId());
        }
        return taxonTsnNodeIdMap;
    }

    private void createReferenceEdges(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        for (final ReferenceLink link : dataSource.referenceLinks) {
            final Node referenceNode = findReferenceNode(graph, link.docIdPrefix, link.documentationId);
            if (referenceNode == null)
                continue;
            final EdgeBuilder builder = graph.buildEdge().fromNode(taxonTsnNodeIdMap.get(link.tsn)).toNode(
                    referenceNode).withLabel("HAS_REFERENCE");
            if (StringUtils.isNotEmpty(link.originalDescInd))
                builder.withProperty("original_desc_ind", link.originalDescInd);
            if (StringUtils.isNotEmpty(link.initItisDescInd))
                builder.withProperty("init_itis_desc_ind", link.initItisDescInd);
            builder.withPropertyIfNotNull("change_track_id", link.changeTrackId);
            builder.build();
        }
    }

    private Node findReferenceNode(final Graph graph, final String docIdPrefix, final int documentationId) {
        if ("PUB".equals(docIdPrefix))
            return graph.findNode(PUBLICATION_LABEL, ID_KEY, documentationId);
        if ("SRC".equals(docIdPrefix))
            return graph.findNode(SOURCE_LABEL, ID_KEY, documentationId);
        if ("EXP".equals(docIdPrefix))
            return graph.findNode(EXPERT_LABEL, ID_KEY, documentationId);
        LOGGER.warn("Unknown reference prefix '" + docIdPrefix + "'");
        return null;
    }

    private void createHierarchyEdges(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        for (final Hierarchy hierarchy : dataSource.hierarchies)
            if (hierarchy.parentTsn != null && hierarchy.parentTsn > 0)
                graph.addEdge(taxonTsnNodeIdMap.get(hierarchy.parentTsn), taxonTsnNodeIdMap.get(hierarchy.tsn),
                              "HAS_CHILD");
    }

    private void createTaxonomicUnitCommentEdges(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        for (final TaxonomicUnitCommentLink link : dataSource.taxonomicUnitCommentLinks)
            graph.addEdge(taxonTsnNodeIdMap.get(link.tsn), graph.findNode(COMMENT_LABEL, ID_KEY, link.commentId),
                          "HAS_COMMENT");
    }

    private void createTaxonomicUnitSynonymEdges(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        for (final Map.Entry<Integer, Integer> link : dataSource.synonymLinks.entrySet())
            graph.addEdge(taxonTsnNodeIdMap.get(link.getValue()), taxonTsnNodeIdMap.get(link.getKey()), "HAS_SYNONYM");
    }

    private void createGeographicDivisionNodes(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        final Set<String> uniqueDivisions = new HashSet<>();
        for (final GeographicDivision division : dataSource.geographicDivisions)
            uniqueDivisions.add(division.value);
        final Map<String, Long> divisionNodeIdMap = new HashMap<>();
        for (final String division : uniqueDivisions) {
            final Node node = graph.addNode(GEOGRAPHIC_DIVISION_LABEL, ID_KEY, division);
            divisionNodeIdMap.put(division, node.getId());
        }
        for (final GeographicDivision division : dataSource.geographicDivisions)
            graph.addEdge(taxonTsnNodeIdMap.get(division.tsn), divisionNodeIdMap.get(division.value),
                          "IN_GEO_DIVISION");
    }

    private void createJurisdictionNodes(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        final Set<String> uniqueJurisdictions = new HashSet<>();
        for (final Jurisdiction jurisdiction : dataSource.jurisdictions)
            uniqueJurisdictions.add(jurisdiction.value);
        final Map<String, Long> jurisdictionNodeIdMap = new HashMap<>();
        for (final String jurisdiction : uniqueJurisdictions) {
            final Node node = graph.addNode(JURISDICTION_LABEL, ID_KEY, jurisdiction);
            jurisdictionNodeIdMap.put(jurisdiction, node.getId());
        }
        for (final Jurisdiction jurisdiction : dataSource.jurisdictions)
            graph.addEdge(taxonTsnNodeIdMap.get(jurisdiction.tsn), jurisdictionNodeIdMap.get(jurisdiction.value),
                          "IN_JURISDICTION", "origin", jurisdiction.origin);
    }

    private Map<Integer, Long> createVernacularNodes(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        final Map<Integer, Long> vernacularIdNodeIdMap = new HashMap<>();
        for (final Vernacular vernacular : dataSource.vernaculars) {
            final Node node = graph.addNodeFromModel(vernacular);
            vernacularIdNodeIdMap.put(vernacular.vernacularId, node.getId());
            graph.addEdge(taxonTsnNodeIdMap.get(vernacular.tsn), node, "HAS_VERNACULAR");
        }
        return vernacularIdNodeIdMap;
    }

    private void createVernacularReferenceEdges(final Graph graph, final Map<Integer, Long> vernacularIdNodeIdMap) {
        final Set<Integer> warnedVernacularIds = new HashSet<>();
        for (final VernacularReferenceLink link : dataSource.vernacularReferenceLinks) {
            final Node referenceNode = findReferenceNode(graph, link.docIdPrefix, link.documentationId);
            if (referenceNode == null)
                continue;
            final Long vernacularNodeId = vernacularIdNodeIdMap.get(link.vernacularId);
            if (vernacularNodeId != null)
                graph.addEdge(vernacularNodeId, referenceNode, "HAS_REFERENCE", "tsn", link.tsn);
            else if (!warnedVernacularIds.contains(link.vernacularId)) {
                LOGGER.warn("Unknown reference to vernacular id '" + link.vernacularId + "'");
                warnedVernacularIds.add(link.vernacularId);
            }
        }
    }
}
