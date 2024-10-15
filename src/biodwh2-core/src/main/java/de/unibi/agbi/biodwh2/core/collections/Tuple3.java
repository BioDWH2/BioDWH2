package de.unibi.agbi.biodwh2.core.collections;

import java.util.Objects;

public final class Tuple3<K, V, W> {
    private final K first;
    private final V second;
    private final W third;

    public Tuple3(final K first, final V second, final W third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public W getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final var tuple2 = (Tuple3<?, ?, ?>) o;
        return Objects.equals(first, tuple2.first) && Objects.equals(second, tuple2.second) && Objects.equals(third,
                                                                                                              tuple2.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "Tuple2{" + "first=" + first + ", second=" + second + ", third=" + third + '}';
    }
}
