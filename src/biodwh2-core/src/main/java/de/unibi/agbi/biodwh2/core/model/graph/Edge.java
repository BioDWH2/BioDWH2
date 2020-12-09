package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreId;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Edge extends MVStoreModel {
    private static final long serialVersionUID = -6592771152520163851L;
    public static final String FROM_ID_FIELD = "__from_id";
    public static final String TO_ID_FIELD = "__to_id";
    public static final String LABEL_FIELD = "__label";
    public static final Set<String> IGNORED_FIELDS = new HashSet<>(
            Arrays.asList(ID_FIELD, LABEL_FIELD, FROM_ID_FIELD, TO_ID_FIELD));

    private Edge() {
        super();
    }

    public static Edge newEdge(final MVStoreId fromId, final MVStoreId toId, final String label) {
        Edge edge = new Edge();
        edge.put(ID_FIELD, new MVStoreId());
        edge.put(FROM_ID_FIELD, fromId);
        edge.put(TO_ID_FIELD, toId);
        edge.put(LABEL_FIELD, label);
        return edge;
    }

    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.writeLong(this.<MVStoreId>getProperty(FROM_ID_FIELD).getIdValue());
        s.writeLong(this.<MVStoreId>getProperty(TO_ID_FIELD).getIdValue());
        s.writeUTF(this.getProperty(LABEL_FIELD));
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        setPropertyUnmodified(FROM_ID_FIELD, new MVStoreId(s.readLong()));
        setPropertyUnmodified(TO_ID_FIELD, new MVStoreId(s.readLong()));
        setPropertyUnmodified(LABEL_FIELD, s.readUTF());
    }

    void resetId() {
        put(ID_FIELD, new MVStoreId());
    }

    public long getFromId() {
        return this.<MVStoreId>getProperty(FROM_ID_FIELD).getIdValue();
    }

    void setFromId(long fromId) {
        put(FROM_ID_FIELD, new MVStoreId(fromId));
    }

    public long getToId() {
        return this.<MVStoreId>getProperty(TO_ID_FIELD).getIdValue();
    }

    void setToId(long toId) {
        put(TO_ID_FIELD, new MVStoreId(toId));
    }

    public String getLabel() {
        return getProperty(LABEL_FIELD);
    }
}
