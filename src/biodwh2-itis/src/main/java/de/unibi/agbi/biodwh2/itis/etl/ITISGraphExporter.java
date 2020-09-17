package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;
import de.unibi.agbi.biodwh2.itis.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ITISGraphExporter extends GraphExporter<ITISDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITISGraphExporter.class);
    private static final String GEOGRAPHIC_DIVISION_LABEL = "GeographicDivision";
    private static final String JURISDICTION_LABEL = "Jurisdiction";
    private static final String EXPERT_LABEL = "Expert";
    private static final String KINGDOM_LABEL = "Kingdom";
    private static final String RANK_LABEL = "Rank";
    private static final String TAXON_LABEL = "Taxon";

    public ITISGraphExporter(final ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id");
        LOGGER.info("Exporting comments...");
        createCommentNodes(graph);
        LOGGER.info("Exporting sources...");
        createSourceNodes(graph);
        LOGGER.info("Exporting kingdoms...");
        createKingdomNodes(graph);
        LOGGER.info("Exporting experts...");
        createExpertNodes(graph);
        LOGGER.info("Exporting ranks...");
        createTaxonUnitTypeNodes(graph);

        LOGGER.info("Exporting taxonomic units...");
        final Map<Integer, Long> taxonTsnNodeIdMap = createTaxonomicUnitNodes(graph);

        LOGGER.info("Exporting geographic divisions...");
        createGeographicDivisionNodes(graph, taxonTsnNodeIdMap);
        LOGGER.info("Exporting jurisdictions...");
        //createJurisdictionNodes(graph, taxonTsnNodeIdMap);
        return true;
    }

    private void createCommentNodes(final Graph graph) {
        for (final Comment comment : dataSource.comments)
            createNodeFromModel(graph, comment);
    }

    private void createSourceNodes(final Graph graph) {
        for (final OtherSource source : dataSource.otherSources)
            createNodeFromModel(graph, source);
    }

    private void createKingdomNodes(final Graph graph) {
        for (final Kingdom kingdom : dataSource.kingdoms)
            graph.buildNode().withLabel(KINGDOM_LABEL).withProperty("id", kingdom.id).withProperty("name", kingdom.name)
                 .build();
    }

    private void createExpertNodes(final Graph graph) {
        for (final Expert expert : dataSource.experts)
            graph.buildNode().withLabel(EXPERT_LABEL).withProperty("id", expert.id).withProperty("name", expert.name)
                 .withProperty("comment", expert.comment).build();
    }

    private void createTaxonUnitTypeNodes(final Graph graph) {
        final Map<Integer, Long> rankNodeIdLookup = new HashMap<>();
        for (final TaxonUnitType rank : dataSource.taxonUnitTypes) {
            final Node node = graph.addNode(RANK_LABEL, "id", rank.id, "name", rank.name);
            rankNodeIdLookup.put(rank.id, node.getId());
            graph.addEdge(graph.findNode(KINGDOM_LABEL, "id", rank.kingdomId), node, "HAS_RANK");
        }
        for (final TaxonUnitType rank : dataSource.taxonUnitTypes) {
            final long nodeId = rankNodeIdLookup.get(rank.id);
            graph.addEdge(nodeId, rankNodeIdLookup.get(rank.dirParentRankId), "HAS_PARENT");
            graph.addEdge(nodeId, rankNodeIdLookup.get(rank.reqParentRankId), "HAS_REQ_PARENT");
        }
    }

    private Map<Integer, Long> createTaxonomicUnitNodes(final Graph graph) {
        final Map<Integer, Long> taxonTsnNodeIdMap = new HashMap<>();
        for (final TaxonomicUnit taxon : dataSource.taxonomicUnits) {
            Node node = graph.addNode(TAXON_LABEL, "id", taxon.tsn);
            taxonTsnNodeIdMap.put(taxon.tsn, node.getId());
        }
        return taxonTsnNodeIdMap;
    }

    private void createGeographicDivisionNodes(final Graph graph, final Map<Integer, Long> taxonTsnNodeIdMap) {
        final Set<String> uniqueDivisions = new HashSet<>();
        for (final GeographicDivision division : dataSource.geographicDivisions)
            uniqueDivisions.add(division.value);
        final Map<String, Long> divisionNodeIdMap = new HashMap<>();
        for (final String division : uniqueDivisions) {
            final Node node = graph.addNode(GEOGRAPHIC_DIVISION_LABEL, "id", division);
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
            final Node node = graph.addNode(JURISDICTION_LABEL, "id", jurisdiction);
            jurisdictionNodeIdMap.put(jurisdiction, node.getId());
        }
        for (final Jurisdiction jurisdiction : dataSource.jurisdictions)
            graph.addEdge(taxonTsnNodeIdMap.get(jurisdiction.tsn), jurisdictionNodeIdMap.get(jurisdiction.value),
                          "IN_JURISDICTION", "origin", jurisdiction.origin);
    }
}
