package de.unibi.agbi.biodwh2.core.collections;

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
    public String toString() {
        return "Tuple2{" + "first=" + first + ", second=" + second + '}';
    }
}
