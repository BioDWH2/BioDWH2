package de.unibi.agbi.biodwh2.core.model.graph;

public final class EdgeBuilder extends ModelBuilder<EdgeBuilder> {
    private static final long serialVersionUID = 8684453285018926870L;

    private long fromId;
    private long toId;

    EdgeBuilder(final Graph graph) {
        super(graph);
    }

    public EdgeBuilder fromNode(Node node) {
        fromId = node.getId();
        return this;
    }

    public EdgeBuilder fromNode(long nodeId) {
        fromId = nodeId;
        return this;
    }

    public EdgeBuilder toNode(Node node) {
        toId = node.getId();
        return this;
    }

    public EdgeBuilder toNode(long nodeId) {
        toId = nodeId;
        return this;
    }

    public Edge build() {
        return graph.addEdge(fromId, toId, label, this);
    }
}
