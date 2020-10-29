package de.unibi.agbi.biodwh2.core.model.graph;

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.io.Serializable;
import java.util.*;

public class Edge implements PropertyContainer, Mappable, Serializable {
    private static final long serialVersionUID = -6592771152520163851L;
    private static final String ID_FIELD = "__id";
    public static final String FROM_ID_FIELD = "__from_id";
    public static final String TO_ID_FIELD = "__to_id";
    public static final String LABEL_FIELD = "__label";
    public static final Set<String> IGNORED_FIELDS = new HashSet<>(
            Arrays.asList(ID_FIELD, LABEL_FIELD, FROM_ID_FIELD, TO_ID_FIELD, "_modified", "_revision", "_id"));

    @Id
    private NitriteId __id;
    private Document document;

    @SuppressWarnings("unused")
    private Edge() {
    }

    Edge(long fromId, long toId, String label) {
        document = new Document();
        document.put(FROM_ID_FIELD, fromId);
        document.put(TO_ID_FIELD, toId);
        document.put(LABEL_FIELD, label);
    }

    ObjectFilter getEqFilter() {
        return ObjectFilters.eq(ID_FIELD, __id);
    }

    void resetId() {
        document.remove(ID_FIELD);
        __id = null;
    }

    public Long getId() {
        return __id == null ? null : __id.getIdValue();
    }

    public long getFromId() {
        return (long) document.get(FROM_ID_FIELD);
    }

    void setFromId(long fromId) {
        document.put(FROM_ID_FIELD, fromId);
    }

    public long getToId() {
        return (long) document.get(TO_ID_FIELD);
    }

    void setToId(long toId) {
        document.put(TO_ID_FIELD, toId);
    }

    @Override
    public String getLabel() {
        return (String) document.get(LABEL_FIELD);
    }

    @Override
    public Collection<String> getPropertyKeys() {
        return document.keySet();
    }

    @Override
    public Map<String, Class<?>> getPropertyKeyTypes() {
        final Map<String, Class<?>> keyTypeMap = new HashMap<>();
        for (final String key : document.keySet())
            if (!IGNORED_FIELDS.contains(key))
                keyTypeMap.put(key, document.get(key) == null ? null : document.get(key).getClass());
        return keyTypeMap;
    }

    @Override
    public <T> T getProperty(String key) {
        //noinspection unchecked
        return (T) document.get(key);
    }

    @Override
    public void setProperty(final String key, final Object value) {
        document.put(key, value);
    }

    @Override
    public boolean hasProperty(final String key) {
        return document.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Edge edge = (Edge) o;
        return document.getId().equals(edge.document.getId());
    }

    @Override
    public int hashCode() {
        return document.getId().hashCode();
    }

    @Override
    public Document write(NitriteMapper nitriteMapper) {
        document.put(ID_FIELD, __id);
        return document;
    }

    @Override
    public void read(NitriteMapper nitriteMapper, Document document) {
        this.document = document;
        __id = NitriteId.createId((long) document.get(ID_FIELD));
    }
}
