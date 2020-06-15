package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;

public interface PropertyContainer {
    void setProperty(final String key, final Object value) throws GraphCacheException;
}
