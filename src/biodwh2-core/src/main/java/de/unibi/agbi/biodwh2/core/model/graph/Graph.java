package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.*;

public class Graph {
    private final Map<Long, Node> idNodeMap;
    private final List<Edge> edges;

    public Graph() {
        idNodeMap = new HashMap<>();
        edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        idNodeMap.put(node.getId(), node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public Collection<Node> getNodes() {
        return idNodeMap.values();
    }

    public Collection<Edge> getEdges() {
        return edges;
    }
}
