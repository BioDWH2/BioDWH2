package de.unibi.agbi.biodwh2.core.collections;

import java.util.Iterator;

public class BatchIterable<T> implements Iterable<T> {
    private final Iterator<T> parent;
    private final int batchSize;

    public BatchIterable(Iterator<T> parent) {
        this.parent = parent;
        batchSize = 10_000;
    }

    public BatchIterable(Iterator<T> parent, int batchSize) {
        this.parent = parent;
        this.batchSize = batchSize;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return parent.hasNext() && counter < batchSize;
            }

            @Override
            public T next() {
                counter++;
                return parent.next();
            }
        };
    }
}
