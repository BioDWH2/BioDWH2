package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;

import java.util.Collection;
import java.util.Map;

public interface PropertyContainer {
    String getLabel();

    void setProperty(final String key, final Object value);

    boolean hasProperty(final String key);

    Collection<String> getPropertyKeys();

    <T> T getProperty(final String key);

    Map<String, Class<?>> getPropertyKeyTypes();
}
