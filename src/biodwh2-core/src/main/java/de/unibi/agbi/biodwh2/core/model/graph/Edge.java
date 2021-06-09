package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreId;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Edge extends MVStoreModel {
    private static final long serialVersionUID = -6592771152520163851L;
    public static final String FROM_ID_FIELD = "__from_id";
    public static final String TO_ID_FIELD = "__to_id";
    public static final String LABEL_FIELD = "__label";
    public static final Set<String> IGNORED_FIELDS = new HashSet<>(
            Arrays.asList(ID_FIELD, LABEL_FIELD, FROM_ID_FIELD, TO_ID_FIELD));

    private Edge() {
        super();
    }

    static Edge newEdge(final long fromId, final long toId, final String label) {
        final Edge edge = new Edge();
        edge.put(ID_FIELD, new MVStoreId().getIdValue());
        edge.put(FROM_ID_FIELD, fromId);
        edge.put(TO_ID_FIELD, toId);
        edge.put(LABEL_FIELD, label);
        return edge;
    }

    void resetId() {
        put(ID_FIELD, new MVStoreId().getIdValue());
    }

    public Long getFromId() {
        return this.getProperty(FROM_ID_FIELD);
    }

    void setFromId(long fromId) {
        put(FROM_ID_FIELD, fromId);
    }

    public Long getToId() {
        return this.getProperty(TO_ID_FIELD);
    }

    void setToId(long toId) {
        put(TO_ID_FIELD, toId);
    }

    public String getLabel() {
        return getProperty(LABEL_FIELD);
    }
}
