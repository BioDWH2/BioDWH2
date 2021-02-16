package de.unibi.agbi.biodwh2.core.io.mvstore;

import de.unibi.agbi.biodwh2.core.collections.ConcurrentDoublyLinkedList;
import de.unibi.agbi.biodwh2.core.collections.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IndexUtilsTest {
    @Test
    void sortPages() {
        final Set<Long> input = new HashSet<>(Arrays.asList(1L, 5L, 4L, 100L, 3L, 87L, 54L, 6L, 2L, 7L, 8L, 9L, 10L));
        final Tuple2<IndexPageMetadata, ConcurrentDoublyLinkedList<Long>>[] output = IndexUtils.sortIdsIntoPages(input, 4);
        assertEquals(4, output.length);
        assertMetadata(output[0].getFirst(), 1L, 4L, 4);
        assertArrayEquals(new Long[]{1L, 2L, 3L, 4L}, output[0].getSecond().toArray(new Long[0]));
        assertMetadata(output[1].getFirst(), 5L, 8L, 4);
        assertArrayEquals(new Long[]{5L, 6L, 7L, 8L}, output[1].getSecond().toArray(new Long[0]));
        assertMetadata(output[2].getFirst(), 9L, 87L, 4);
        assertArrayEquals(new Long[]{9L, 10L, 54L, 87L}, output[2].getSecond().toArray(new Long[0]));
        assertMetadata(output[3].getFirst(), 100L, 100L, 1);
        assertArrayEquals(new Long[]{100L}, output[3].getSecond().toArray(new Long[0]));
    }

    private void assertMetadata(final IndexPageMetadata metadata, final long expectedMinId, final long expectedMaxId,
                                final int expectedSlotsUsed) {
        assertEquals(expectedSlotsUsed, metadata.slotsUsed);
        assertEquals(expectedMinId, metadata.minId);
        assertEquals(expectedMaxId, metadata.maxId);
    }
}
