package de.unibi.agbi.biodwh2.core.io.mvstore;

import de.unibi.agbi.biodwh2.core.collections.ConcurrentDoublyLinkedList;
import de.unibi.agbi.biodwh2.core.collections.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class IndexUtils {
    private IndexUtils() {
    }

    public static Tuple2<IndexPageMetadata, ConcurrentDoublyLinkedList<Long>>[] sortIdsIntoPages(final Set<Long> ids,
                                                                                                 final int pageSize) {
        final int pageCountNeeded = (int) Math.ceil(ids.size() / (float) pageSize);
        final List<Tuple2<IndexPageMetadata, ConcurrentDoublyLinkedList<Long>>> result = new ArrayList<>(
                pageCountNeeded);
        final Long[] sortedIds = ids.stream().sorted().toArray(Long[]::new);
        for (int i = 0; i < sortedIds.length; i += pageSize) {
            final int size = Math.min(pageSize, sortedIds.length - i);
            final ConcurrentDoublyLinkedList<Long> page = createListFromSubArray(sortedIds, i, size);
            result.add(new Tuple2<>(createMetadataFromPage(page), page));
        }
        //noinspection unchecked
        return result.toArray(new Tuple2[0]);
    }

    private static ConcurrentDoublyLinkedList<Long> createListFromSubArray(final Long[] source, final int start,
                                                                           final int length) {
        final ConcurrentDoublyLinkedList<Long> target = new ConcurrentDoublyLinkedList<>();
        final int end = start + length;
        for (int i = start; i < end; i++)
            target.addLast(source[i]);
        return target;
    }

    private static IndexPageMetadata createMetadataFromPage(final ConcurrentDoublyLinkedList<Long> page) {
        final IndexPageMetadata metadata = new IndexPageMetadata();
        metadata.minId = page.getFirst();
        metadata.maxId = page.getLast();
        metadata.slotsUsed = page.size();
        return metadata;
    }
}
