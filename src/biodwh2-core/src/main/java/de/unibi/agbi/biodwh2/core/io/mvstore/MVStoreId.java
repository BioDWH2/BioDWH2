package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class MVStoreId implements Comparable<MVStoreId>, Serializable {
    private static final long serialVersionUID = 3433065226665631299L;
    private static final AtomicLong counter = new AtomicLong(System.nanoTime());
    private final long idValue;

    public MVStoreId() {
        this.idValue = counter.getAndIncrement();
    }

    public MVStoreId(final Long value) {
        this.idValue = value;
    }

    public int compareTo(final MVStoreId other) {
        if (other != null)
            return Long.compare(this.idValue, other.idValue);
        throw new IllegalArgumentException("other");
    }

    public String toString() {
        return String.valueOf(idValue);
    }

    public Long getIdValue() {
        return this.idValue;
    }

    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MVStoreId))
            return false;
        Long otherIdValue = ((MVStoreId) o).getIdValue();
        return Objects.equals(idValue, otherIdValue);
    }

    public int hashCode() {
        return Objects.hashCode(idValue);
    }
}
