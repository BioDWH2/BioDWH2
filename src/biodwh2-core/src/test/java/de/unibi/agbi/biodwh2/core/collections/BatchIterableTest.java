package de.unibi.agbi.biodwh2.core.collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BatchIterableTest {
    @Test
    public void test() {
        final var list = new ArrayList<Integer>();
        for (int i = 0; i < 20; i++)
            list.add(i);
        var batchIterator = new BatchIterable<>(list.iterator(), 5);
        for (int i = 0; i < 4; i++) {
            int counter = 0;
            for (final var value : batchIterator) {
                assertEquals(list.get(i * 5 + counter), value);
                counter++;
            }
        }
        assertFalse(batchIterator.iterator().hasNext());
    }
}