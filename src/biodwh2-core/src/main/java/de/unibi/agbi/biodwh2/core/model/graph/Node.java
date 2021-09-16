package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreId;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Node extends MVStoreModel {
    private static final long serialVersionUID = -5027987220033105538L;
    public static final String LABEL_FIELD = "__label";
    public static final Set<String> IGNORED_FIELDS = new HashSet<>(Arrays.asList(ID_FIELD, LABEL_FIELD));

    private Node() {
        super();
    }

    static Node newNode(final String label) {
        final Node node = new Node();
        node.put(ID_FIELD, new MVStoreId().getIdValue());
        node.put(LABEL_FIELD, label);
        return node;
    }

    void resetId() {
        put(ID_FIELD, new MVStoreId().getIdValue());
    }

    public String getLabel() {
        return getProperty(LABEL_FIELD);
    }
}
