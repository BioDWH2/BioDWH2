package de.unibi.agbi.biodwh2.core.model.graph;

public final class NodeBuilder extends ModelBuilder<NodeBuilder> {
    private static final long serialVersionUID = 9026156747952856158L;

    NodeBuilder(final Graph graph) {
        super(graph);
    }

    public Node build() {
        return graph.addNode(label, this);
    }
}
