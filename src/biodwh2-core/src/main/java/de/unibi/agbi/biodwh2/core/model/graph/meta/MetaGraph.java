package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.model.graph.BaseGraph;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.util.*;

public final class MetaGraph {
    private static final String METADATA_LABEL = "metadata";

    private final List<String> dataSourceIds = new ArrayList<>();
    private final boolean isMappedGraph;
    private final Map<String, MetaNode> nodes = new HashMap<>();
    private final Map<String, MetaEdge> edges = new HashMap<>();

    public MetaGraph(final BaseGraph graph) {
        collectDataSourceIds(graph);
        isMappedGraph = determineIsMappedGraph(graph);
        addMetaNodes(graph);
        addMetaEdges(graph);
    }

    private void collectDataSourceIds(final BaseGraph graph) {
        if (Arrays.asList(graph.getNodeLabels()).contains(METADATA_LABEL))
            for (final Node node : graph.getNodes(METADATA_LABEL))
                dataSourceIds.add(node.getProperty("datasource_id"));
        dataSourceIds.sort(String::compareTo);
    }

    private boolean determineIsMappedGraph(final BaseGraph graph) {
        for (final String label : graph.getNodeLabels())
            for (final String dataSourceId : dataSourceIds)
                if (label.startsWith(dataSourceId + '_'))
                    return true;
        return false;
    }

    private void addMetaNodes(final BaseGraph graph) {
        for (final String label : graph.getNodeLabels()) {
            final String dataSourceId = determineDataSourceIdForNodeLabel(label);
            final boolean isMappingLabel = isMappedGraph && dataSourceId == null && !METADATA_LABEL.equals(label);
            final MetaNode node = new MetaNode(label, dataSourceId, isMappingLabel);
            node.count = graph.getNumberOfNodes(label);
            nodes.put(label, node);
        }
    }

    private String determineDataSourceIdForNodeLabel(final String label) {
        if (METADATA_LABEL.equals(label))
            return null;
        if (label.contains("_"))
            for (final String dataSourceId : dataSourceIds)
                if (label.startsWith(dataSourceId + '_'))
                    return dataSourceId;
        return null;
    }

    private void addMetaEdges(final BaseGraph graph) {
        for (final String label : graph.getEdgeLabels()) {
            for (final Edge edge : graph.getEdges(label)) {
                final String fromLabel = graph.getNodeLabel(edge.getFromId());
                final String toLabel = graph.getNodeLabel(edge.getToId());
                final String key = label + '|' + fromLabel + '|' + toLabel;
                MetaEdge metaEdge = edges.get(key);
                if (metaEdge == null) {
                    metaEdge = new MetaEdge(fromLabel, toLabel, label);
                    edges.put(key, metaEdge);
                }
                metaEdge.count++;
            }
        }
    }

    public long getNodeLabelCount() {
        return nodes.size();
    }

    public long getTotalNodeCount() {
        return nodes.values().stream().map(n -> n.count).reduce(0L, Long::sum);
    }

    public long getEdgeLabelCount() {
        return edges.values().stream().map(e -> e.label).distinct().count();
    }

    public long getTotalEdgeCount() {
        return edges.values().stream().map(e -> e.count).reduce(0L, Long::sum);
    }

    public Collection<MetaNode> getNodes() {
        return nodes.values();
    }

    public Collection<MetaEdge> getEdges() {
        return edges.values();
    }

    public Collection<String> getDataSourceIds() {
        return new ArrayList<>(dataSourceIds);
    }

    public boolean isMappedGraph() {
        return isMappedGraph;
    }
}
