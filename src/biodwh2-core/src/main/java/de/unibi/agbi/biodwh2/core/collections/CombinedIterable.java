package de.unibi.agbi.biodwh2.core.collections;

import java.util.Iterator;

public class CombinedIterable<T> implements Iterable<T> {
    private final Iterable<Iterable<T>> iterables;

    public CombinedIterable(final Iterable<Iterable<T>> iterables) {
        this.iterables = iterables;
    }

    @Override
    public Iterator<T> iterator() {
        return new CombinedIterator<>(iterables);
    }
}
