package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class MVStoreId implements Comparable<Object>, Serializable {
    private static final long serialVersionUID = 3433065226665631299L;
    private static final AtomicLong COUNTER = new AtomicLong(System.nanoTime());
    private final long idValue;

    public MVStoreId() {
        this.idValue = COUNTER.getAndIncrement();
    }

    public MVStoreId(final Long value) {
        this.idValue = value;
    }

    @Override
    public int compareTo(final Object other) {
        if (other != null) {
            if (other instanceof MVStoreId)
                return Long.compare(this.idValue, ((MVStoreId) other).idValue);
            else if (other instanceof Long)
                return Long.compare(this.idValue, (Long) other);
        }
        throw new IllegalArgumentException("other");
    }

    @Override
    public String toString() {
        return String.valueOf(idValue);
    }

    public Long getIdValue() {
        return this.idValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (o instanceof MVStoreId)
            return Objects.equals(idValue, ((MVStoreId) o).getIdValue());
        if (o instanceof Long)
            return Objects.equals(idValue, o);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idValue);
    }
}
