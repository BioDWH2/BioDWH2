package de.unibi.agbi.biodwh2.core.collections;

import java.util.Iterator;

public class CombinedIterator<T> implements Iterator<T> {
    private Iterator<T> current;
    private final Iterator<Iterable<T>> iterables;

    public CombinedIterator(final Iterable<Iterable<T>> iterables) {
        this.iterables = iterables.iterator();
    }

    @Override
    public boolean hasNext() {
        advanceIteratorIfNeeded();
        return current != null && current.hasNext();
    }

    private void advanceIteratorIfNeeded() {
        while ((current == null || !current.hasNext()) && iterables.hasNext())
            current = iterables.next().iterator();
    }

    @Override
    public T next() {
        advanceIteratorIfNeeded();
        return current != null ? current.next() : null;
    }
}
