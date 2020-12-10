package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreId;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Node extends MVStoreModel {
    private static final long serialVersionUID = -5027987220033105538L;
    public static final String LABELS_FIELD = "__labels";
    public static final Set<String> IGNORED_FIELDS = new HashSet<>(Arrays.asList(ID_FIELD, LABELS_FIELD));

    private Node() {
        super();
    }

    public static Node newNode(final String... labels) {
        Node node = new Node();
        node.put(ID_FIELD, new MVStoreId().getIdValue());
        node.put(LABELS_FIELD, labels);
        return node;
    }

    public static NodeBuilder newNodeBuilder(final Graph graph) {
        return new NodeBuilder(graph);
    }

    void resetId() {
        put(ID_FIELD, new MVStoreId().getIdValue());
    }

    public String[] getLabels() {
        return getProperty(LABELS_FIELD);
    }
}
