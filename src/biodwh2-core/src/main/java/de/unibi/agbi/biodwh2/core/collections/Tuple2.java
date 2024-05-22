package de.unibi.agbi.biodwh2.core.collections;

import java.util.Objects;

public final class Tuple2<K, V> {
    private final K first;
    private final V second;

    public Tuple2(final K first, final V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final var tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(first, tuple2.first) && Objects.equals(second, tuple2.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Tuple2{" + "first=" + first + ", second=" + second + '}';
    }
}
