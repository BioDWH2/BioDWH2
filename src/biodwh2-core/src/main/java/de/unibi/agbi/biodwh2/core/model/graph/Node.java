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

public class Node implements PropertyContainer, Map<String, Object>, Mappable, Serializable {
    private static final long serialVersionUID = -5027987220033105538L;
    private static final String ID_FIELD = "__id";
    static final String LABEL_FIELD = "__label";
    public static final Set<String> IGNORED_FIELDS = new HashSet<>(
            Arrays.asList(ID_FIELD, LABEL_FIELD, "_modified", "_revision", "_id"));

    @Id
    private NitriteId __id;
    private Document document;

    @SuppressWarnings("unused")
    private Node() {
    }

    Node(final String label) {
        document = new Document();
        document.put(LABEL_FIELD, label);
    }

    ObjectFilter getEqFilter() {
        return ObjectFilters.eq(ID_FIELD, __id);
    }

    void resetId() {
        document.remove(ID_FIELD);
        __id = null;
    }

    void prefixLabel(final String prefix) {
        document.put(LABEL_FIELD, prefix + getLabel());
    }

    public Long getId() {
        return __id == null ? null : __id.getIdValue();
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
    public int size() {
        return document.size();
    }

    @Override
    public boolean isEmpty() {
        return document.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return document.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return document.containsValue(o);
    }

    @Override
    public Object get(Object o) {
        return document.get(o);
    }

    @Override
    public Object put(String s, Object o) {
        return document.put(s, o);
    }

    @Override
    public Object remove(Object o) {
        return document.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        document.putAll(map);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Clear is not permitted on Nodes");
    }

    @Override
    public Set<String> keySet() {
        return document.keySet();
    }

    @Override
    public Collection<Object> values() {
        return document.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return document.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Node node = (Node) o;
        return document.getId().equals(node.document.getId());
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
