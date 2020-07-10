package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.*;

public class Node implements PropertyContainer, Mappable {
    private static final String IdField = "__id";
    static final String LabelField = "__label";
    public static final Set<String> IgnoredFields = new HashSet<>(
            Arrays.asList(IdField, LabelField, "_modified", "_revision", "_id"));

    @Id
    private NitriteId __id;
    private Document document;

    @SuppressWarnings("unused")
    private Node() {
    }

    Node(final String label) {
        document = new Document();
        document.put(LabelField, label);
    }

    ObjectFilter getEqFilter() {
        return ObjectFilters.eq(IdField, __id);
    }

    void resetId() {
        document.remove(IdField);
        __id = null;
    }

    void prefixLabel(final String prefix) {
        document.put(LabelField, prefix + getLabel());
    }

    public Long getId() {
        return __id != null ? __id.getIdValue() : null;
    }

    public String getLabel() {
        return (String) document.get(LabelField);
    }

    public Collection<String> getPropertyKeys() {
        return document.keySet();
    }

    public Map<String, Class<?>> getPropertyKeyTypes() {
        Map<String, Class<?>> keyTypeMap = new HashMap<>();
        for (String key : document.keySet())
            if (!IgnoredFields.contains(key))
                keyTypeMap.put(key, document.get(key) != null ? document.get(key).getClass() : null);
        return keyTypeMap;
    }

    public <T> T getProperty(String key) {
        //noinspection unchecked
        return (T) document.get(key);
    }

    public void setProperty(final String key, final Object value) throws GraphCacheException {
        document.put(key, value);
    }

    public boolean hasProperty(final String key) {
        return document.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return document.getId().equals(node.document.getId());
    }

    @Override
    public int hashCode() {
        return document.getId().hashCode();
    }

    @Override
    public Document write(NitriteMapper nitriteMapper) {
        document.put(IdField, __id);
        return document;
    }

    @Override
    public void read(NitriteMapper nitriteMapper, Document document) {
        this.document = document;
        __id = NitriteId.createId((long) document.get(IdField));
    }
}
