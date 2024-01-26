package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class Graph extends MVStoreGraph {
    public Graph(final String filePath) {
        this(Paths.get(filePath), false, false);
    }

    public Graph(final Path filePath) {
        this(filePath, false, false);
    }

    public Graph(final Path filePath, final boolean reopen) {
        this(filePath, reopen, false);
    }

    public Graph(final Path filePath, final boolean reopen, final boolean readOnly) {
        super(filePath, reopen, readOnly);
    }

    public Node addNode(final String label) {
        final Node n = Node.newNode(label);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey, final Object propertyValue) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey, propertyValue);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                        final Object propertyValue3) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                        final Object propertyValue3, final String propertyKey4, final Object propertyValue4) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        n.setProperty(propertyKey4, propertyValue4);
        update(n);
        return n;
    }

    public Node addNode(final String label, final String propertyKey1, final Object propertyValue1,
                        final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                        final Object propertyValue3, final String propertyKey4, final Object propertyValue4,
                        final String propertyKey5, final Object propertyValue5) {
        final Node n = Node.newNode(label);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        n.setProperty(propertyKey4, propertyValue4);
        n.setProperty(propertyKey5, propertyValue5);
        update(n);
        return n;
    }

    public Node addNode(final String label, final Map<String, Object> properties) {
        final Node n = Node.newNode(label);
        for (final Map.Entry<String, Object> entry : properties.entrySet())
            n.setProperty(entry.getKey(), entry.getValue());
        update(n);
        return n;
    }

    public NodeBuilder buildNode() {
        return new NodeBuilder(this);
    }

    public <T> Node addNodeFromModel(final T obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Node n = Node.newNode(mapping.label);
        mapping.setModelProperties(n, obj);
        update(n);
        return n;
    }

    public <T> Node addNodeFromModel(final T obj, final String propertyKey, final Object propertyValue) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Node n = Node.newNode(mapping.label);
        mapping.setModelProperties(n, obj);
        n.setProperty(propertyKey, propertyValue);
        update(n);
        return n;
    }

    public <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                     final String propertyKey2, final Object propertyValue2) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Node n = Node.newNode(mapping.label);
        mapping.setModelProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        update(n);
        return n;
    }

    public <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                     final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                                     final Object propertyValue3) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Node n = Node.newNode(mapping.label);
        mapping.setModelProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        update(n);
        return n;
    }

    public <T> Node addNodeFromModel(final T obj, final String propertyKey1, final Object propertyValue1,
                                     final String propertyKey2, final Object propertyValue2, final String propertyKey3,
                                     final Object propertyValue3, final String propertyKey4,
                                     final Object propertyValue4) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Node n = Node.newNode(mapping.label);
        mapping.setModelProperties(n, obj);
        n.setProperty(propertyKey1, propertyValue1);
        n.setProperty(propertyKey2, propertyValue2);
        n.setProperty(propertyKey3, propertyValue3);
        n.setProperty(propertyKey4, propertyValue4);
        update(n);
        return n;
    }

    public <T> Node addNodeFromModel(final T obj, final Map<String, Object> additionalProperties) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Node n = Node.newNode(mapping.label);
        mapping.setModelProperties(n, obj);
        if (additionalProperties != null)
            for (final String key : additionalProperties.keySet())
                n.setProperty(key, additionalProperties.get(key));
        update(n);
        return n;
    }

    public Edge addEdge(final Node from, final Node to, final String label) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label);
    }

    private void validateSourceNode(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to add edge because the source node is null");
    }

    private void validateTargetNode(final Node node) {
        if (node == null)
            throw new GraphCacheException("Failed to add edge because the target node is null");
    }

    public Edge addEdge(final long fromId, final Node to, final String label) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label);
    }

    public Edge addEdge(final Node from, final long toId, final String label) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label);
    }

    public Edge addEdge(final long fromId, final long toId, final String label) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey,
                        final Object propertyValue) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, propertyKey, propertyValue);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey,
                        final Object propertyValue) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, propertyKey, propertyValue);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey,
                        final Object propertyValue) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, propertyKey, propertyValue);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey,
                        final Object propertyValue) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey, propertyValue);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, propertyKey1, propertyValue1, propertyKey2, propertyValue2);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey1, propertyValue1);
        e.setProperty(propertyKey2, propertyValue2);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2,
                       propertyKey3, propertyValue3);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2,
                       propertyKey3, propertyValue3);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, propertyKey1, propertyValue1, propertyKey2, propertyValue2,
                       propertyKey3, propertyValue3);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey1, propertyValue1);
        e.setProperty(propertyKey2, propertyValue2);
        e.setProperty(propertyKey3, propertyValue3);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3, final String propertyKey4,
                        final Object propertyValue4) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2,
                       propertyKey3, propertyValue3, propertyKey4, propertyValue4);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3, final String propertyKey4,
                        final Object propertyValue4) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, propertyKey1, propertyValue1, propertyKey2, propertyValue2,
                       propertyKey3, propertyValue3, propertyKey4, propertyValue4);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3, final String propertyKey4,
                        final Object propertyValue4) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, propertyKey1, propertyValue1, propertyKey2, propertyValue2,
                       propertyKey3, propertyValue3, propertyKey4, propertyValue4);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final String propertyKey1,
                        final Object propertyValue1, final String propertyKey2, final Object propertyValue2,
                        final String propertyKey3, final Object propertyValue3, final String propertyKey4,
                        final Object propertyValue4) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        e.setProperty(propertyKey1, propertyValue1);
        e.setProperty(propertyKey2, propertyValue2);
        e.setProperty(propertyKey3, propertyValue3);
        e.setProperty(propertyKey4, propertyValue4);
        update(e);
        return e;
    }

    public Edge addEdge(final Node from, final Node to, final String label, final Map<String, Object> properties) {
        validateSourceNode(from);
        validateTargetNode(to);
        return addEdge(from.getId(), to.getId(), label, properties);
    }

    public Edge addEdge(final long fromId, final Node to, final String label, final Map<String, Object> properties) {
        validateTargetNode(to);
        return addEdge(fromId, to.getId(), label, properties);
    }

    public Edge addEdge(final Node from, final long toId, final String label, final Map<String, Object> properties) {
        validateSourceNode(from);
        return addEdge(from.getId(), toId, label, properties);
    }

    public Edge addEdge(final long fromId, final long toId, final String label, final Map<String, Object> properties) {
        final Edge e = Edge.newEdge(fromId, toId, label);
        for (final Map.Entry<String, Object> entry : properties.entrySet())
            e.setProperty(entry.getKey(), entry.getValue());
        update(e);
        return e;
    }

    public EdgeBuilder buildEdge() {
        return new EdgeBuilder(this);
    }

    public <T> Edge addEdgeFromModel(final Node from, final Node to, final T obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Edge e = Edge.newEdge(from.getId(), to.getId(), mapping.label);
        mapping.setModelProperties(e, obj);
        update(e);
        return e;
    }

    public <T> Edge addEdgeFromModel(final Node from, final long toId, final T obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Edge e = Edge.newEdge(from.getId(), toId, mapping.label);
        mapping.setModelProperties(e, obj);
        update(e);
        return e;
    }

    public <T> Edge addEdgeFromModel(final long fromId, final Node to, final T obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Edge e = Edge.newEdge(fromId, to.getId(), mapping.label);
        mapping.setModelProperties(e, obj);
        update(e);
        return e;
    }

    public <T> Edge addEdgeFromModel(final long fromId, final long toId, final T obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Edge e = Edge.newEdge(fromId, toId, mapping.label);
        mapping.setModelProperties(e, obj);
        update(e);
        return e;
    }

    public <T> Edge addEdgeFromModel(final long fromId, final long toId, final T obj, final String propertyKey1,
                                     final Object propertyValue1) {
        final ClassMapping mapping = ClassMapping.get(obj);
        final Edge e = Edge.newEdge(fromId, toId, mapping.label);
        mapping.setModelProperties(e, obj);
        e.setProperty(propertyKey1, propertyValue1);
        update(e);
        return e;
    }

    public static Graph createTempGraph() throws IOException {
        final Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        return new Graph(tempFilePath.toString());
    }
}
