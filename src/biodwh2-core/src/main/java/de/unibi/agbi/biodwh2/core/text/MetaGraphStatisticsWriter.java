package de.unibi.agbi.biodwh2.core.text;

import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaEdge;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaNode;

import java.util.*;
import java.util.stream.Collectors;

public final class MetaGraphStatisticsWriter {
    private final MetaGraph graph;

    public MetaGraphStatisticsWriter(final MetaGraph graph) {
        this.graph = graph;
    }

    public String write() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Meta graph statistics [").append(graph.getTotalNodeCount()).append(" nodes across ").append(
                graph.getNodeLabelCount()).append(" labels, ").append(graph.getTotalEdgeCount()).append(
                " edges across ").append(graph.getEdgeLabelCount()).append(" labels]");
        final TableFormatter tableFormatter = new TableFormatter(false);
        final List<List<String>> nodeLabelCountRows = new ArrayList<>();
        for (final MetaNode node : getLabelSortedMetaNodes())
            nodeLabelCountRows.add(Arrays.asList(String.valueOf(node.count), node.label));
        builder.append(tableFormatter.format(Arrays.asList("Count", "Node label"), nodeLabelCountRows));
        if (graph.getEdgeLabelCount() > 0) {
            final List<List<String>> edgeLabelCountRows = new ArrayList<>();
            for (final MetaEdge edge : getLabelSortedMetaEdges()) {
                final String relationship = "(" + edge.fromLabel + ")-[" + edge.label + "]->(" + edge.toLabel + ")";
                edgeLabelCountRows.add(Arrays.asList(String.valueOf(edge.count), relationship));
            }
            builder.append(tableFormatter.format(Arrays.asList("Count", "Edge relationship"), edgeLabelCountRows));
        }
        return builder.toString();
    }

    private Collection<MetaNode> getLabelSortedMetaNodes() {
        return graph.getNodes().stream().sorted(Comparator.comparing(a -> a.label)).collect(Collectors.toList());
    }

    private Collection<MetaEdge> getLabelSortedMetaEdges() {
        return graph.getEdges().stream().sorted(
                Comparator.comparing(a -> a.label + "|" + a.fromLabel + "|" + a.toLabel)).collect(Collectors.toList());
    }
}
