package de.unibi.agbi.biodwh2.core.collections;

import com.fasterxml.jackson.databind.MappingIterator;

import java.io.IOException;
import java.util.Iterator;

public class MappingIterable<T> implements Iterable<T>, AutoCloseable {
    private final MappingIterator<T> iterator;

    public MappingIterable(final MappingIterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public void close() throws IOException {
        iterator.close();
    }
}
