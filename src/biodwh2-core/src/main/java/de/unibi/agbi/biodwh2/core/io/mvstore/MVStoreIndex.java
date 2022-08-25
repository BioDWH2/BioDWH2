package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public abstract class MVStoreIndex {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MVStoreIndex.class);

    protected final String name;
    protected final String key;
    protected final boolean arrayIndex;
    protected final boolean readOnly;
    private final MVIndexDescription indexDescription;

    protected MVStoreIndex(final String name, final String key, final boolean arrayIndex, final boolean readOnly) {
        this.name = name;
        this.key = key;
        this.arrayIndex = arrayIndex;
        this.readOnly = readOnly;
        indexDescription = new MVIndexDescription(key, arrayIndex, getType());
    }

    public final String getName() {
        return name;
    }

    public final String getKey() {
        return key;
    }

    public final boolean isArrayIndex() {
        return arrayIndex;
    }

    public abstract MVStoreIndexType getType();

    public abstract Set<Long> find(final Comparable<?> propertyValue);

    public abstract void remove(final Object propertyValue, final long id);

    public abstract void put(final Object propertyValue, final long id);

    public abstract boolean contains(final Comparable<?> propertyValue);

    public final MVIndexDescription getIndexDescription() {
        return indexDescription;
    }

    public abstract void beginDelay();

    public abstract void endDelay();
}
