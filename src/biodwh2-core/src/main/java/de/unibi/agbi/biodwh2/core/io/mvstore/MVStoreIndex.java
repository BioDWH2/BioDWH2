package de.unibi.agbi.biodwh2.core.io.mvstore;

import de.unibi.agbi.biodwh2.core.collections.ConcurrentDoublyLinkedList;
import org.h2.mvstore.MVMap;

import java.util.*;

public final class MVStoreIndex {
    private static class PageMetadata {
        public Long minId;
        public Long maxId;
        public int slotsUsed;
    }

    public static final int DEFAULT_PAGE_SIZE = 1000;

    private final String name;
    private final String key;
    private final boolean arrayIndex;
    private final int pageSize;
    private final MVMap<Comparable<?>, ConcurrentDoublyLinkedList<Long>> map;
    private final MVMap<Long, ConcurrentDoublyLinkedList<Long>> pagesMap;
    private final Map<Long, PageMetadata> pagesMetadataMap;
    private long nextPageIndex;

    public MVStoreIndex(final MVStoreDB db, final String name, final String key, final boolean arrayIndex) {
        this(db, name, key, arrayIndex, DEFAULT_PAGE_SIZE);
    }

    MVStoreIndex(final MVStoreDB db, final String name, final String key, final boolean arrayIndex,
                 final int pageSize) {
        this.name = name;
        this.key = key;
        this.arrayIndex = arrayIndex;
        this.pageSize = pageSize;
        map = db.openMap(name);
        pagesMap = db.openMap(name + "!pages");
        pagesMetadataMap = new HashMap<>();
        nextPageIndex = 0;
        for (final Long pageIndex : pagesMap.keySet()) {
            nextPageIndex = pageIndex + 1;
            final PageMetadata metadata = new PageMetadata();
            pagesMetadataMap.put(pageIndex, metadata);
        }
        sortAllPages();
    }

    public String getName() {
        return name;
    }

    public void put(final Object key, final MVStoreId id) {
        if (arrayIndex)
            put((Comparable<?>[]) key, id.getIdValue());
        else
            put((Comparable<?>) key, id.getIdValue());
    }

    private void put(final Comparable<?> indexKey, final long id) {
        if (indexKey != null)
            insertToPage(indexKey, id);
    }

    private synchronized void insertToPage(final Comparable<?> indexKey, final long id) {
        ConcurrentDoublyLinkedList<Long> pages = map.get(indexKey);
        boolean pagesChanged = pages == null;
        if (pagesChanged)
            pages = new ConcurrentDoublyLinkedList<>();
        final Long matchedPage = findMatchingPage(pages, id);
        if (matchedPage == null) {
            createNewPage(pages, id);
            pagesChanged = true;
        } else {
            final PageMetadata metadata = pagesMetadataMap.get(matchedPage);
            final ConcurrentDoublyLinkedList<Long> page = pagesMap.get(matchedPage);
            if (!page.contains(id)) {
                page.add(id);
                metadata.slotsUsed++;
                if (metadata.maxId < id)
                    metadata.maxId = id;
                pagesMap.put(matchedPage, page);
            }
        }
        if (pagesChanged)
            map.put(indexKey, pages);
    }

    private Long findMatchingPage(final ConcurrentDoublyLinkedList<Long> pages, final long id) {
        for (final Long pageIndex : pages) {
            final PageMetadata metadata = pagesMetadataMap.get(pageIndex);
            if (metadata.minId != null && metadata.minId <= id &&
                (metadata.maxId > id || metadata.slotsUsed < pageSize))
                return pageIndex;
        }
        return null;
    }

    private void createNewPage(final ConcurrentDoublyLinkedList<Long> pages, final long id) {
        final ConcurrentDoublyLinkedList<Long> page = new ConcurrentDoublyLinkedList<>();
        page.add(id);
        final PageMetadata metadata = new PageMetadata();
        metadata.slotsUsed = 1;
        metadata.minId = metadata.maxId = id;
        pagesMap.put(nextPageIndex, page);
        pagesMetadataMap.put(nextPageIndex, metadata);
        pages.add(nextPageIndex);
        nextPageIndex++;
    }

    private void put(final Comparable<?>[] indexKeys, final long id) {
        for (final Comparable<?> indexKey : indexKeys)
            if (indexKey != null)
                insertToPage(indexKey, id);
    }

    public Set<Long> find(final Comparable<?> indexKey) {
        final ConcurrentDoublyLinkedList<Long> pages = map.get(indexKey);
        if (pages == null)
            return new HashSet<>();
        final Set<Long> idSet = new HashSet<>();
        for (final Long pageIndex : pages)
            idSet.addAll(pagesMap.get(pageIndex));
        return idSet;
    }

    public String getKey() {
        return key;
    }

    public boolean isArrayIndex() {
        return arrayIndex;
    }

    public void remove(final Object key, final MVStoreId id) {
        if (arrayIndex)
            remove((Comparable<?>[]) key, id.getIdValue());
        else
            remove((Comparable<?>) key, id.getIdValue());
    }

    private void remove(final Comparable<?> indexKey, final long id) {
        if (indexKey != null)
            removeFromPage(indexKey, id);
    }

    private synchronized void removeFromPage(final Comparable<?> indexKey, final long id) {
        final ConcurrentDoublyLinkedList<Long> pages = map.get(indexKey);
        if (pages == null)
            return;
        Long pageIndexToRemove = null;
        for (final Long pageIndex : pages) {
            final PageMetadata metadata = pagesMetadataMap.get(pageIndex);
            if (metadata.minId == null || metadata.minId > id || metadata.maxId < id)
                continue;
            final ConcurrentDoublyLinkedList<Long> page = pagesMap.get(pageIndex);
            page.remove(id);
            metadata.slotsUsed--;
            if (metadata.slotsUsed == 0) {
                pageIndexToRemove = pageIndex;
            } else {
                metadata.minId = 0L;
                metadata.maxId = 0L;
                for (final Long value : page) {
                    metadata.minId = Math.min(value, metadata.minId);
                    metadata.maxId = Math.max(value, metadata.maxId);
                }
                pagesMap.put(pageIndex, page);
            }
            break;
        }
        if (pageIndexToRemove != null) {
            pagesMap.remove(pageIndexToRemove);
            pages.remove(pageIndexToRemove);
            pagesMetadataMap.remove(pageIndexToRemove);
            map.put(indexKey, pages);
        }
    }

    private void remove(final Comparable<?>[] indexKeys, final long id) {
        for (final Comparable<?> indexKey : indexKeys)
            if (indexKey != null)
                removeFromPage(indexKey, id);
    }

    private void sortAllPages() {
        for (final Comparable<?> key : map.keySet())
            sortKeyPages(key);
    }

    private void sortKeyPages(final Comparable<?> key) {
        final ConcurrentDoublyLinkedList<Long> pageIndicesList = map.get(key);
        if (pageIndicesList == null || pageIndicesList.size() == 0)
            return;
        final Long[] pageIndices = pageIndicesList.stream().sorted().toArray(Long[]::new);
        final Set<Long> ids = new HashSet<>();
        for (final Long pageIndex : pageIndices)
            ids.addAll(pagesMap.get(pageIndex));
        final Long[] sortedIds = ids.stream().sorted().toArray(Long[]::new);
        int nextPageIndex = 0;
        for (int i = 0; i < sortedIds.length; i += pageSize) {
            final Long pageIndex = pageIndices[nextPageIndex];
            final int size = Math.min(pageSize, sortedIds.length - i);
            final ConcurrentDoublyLinkedList<Long> page = createListFromSubArray(sortedIds, i, size);
            final PageMetadata metadata = pagesMetadataMap.get(pageIndex);
            metadata.minId = page.getFirst();
            metadata.maxId = page.getLast();
            metadata.slotsUsed = size;
            pagesMetadataMap.put(pageIndex, metadata);
            pagesMap.put(pageIndex, page);
            nextPageIndex++;
        }
        if (nextPageIndex < pageIndices.length)
            map.put(key, createListFromSubArray(pageIndices, 0, nextPageIndex));
    }

    private ConcurrentDoublyLinkedList<Long> createListFromSubArray(final Long[] source, final int start,
                                                                    final int length) {
        final ConcurrentDoublyLinkedList<Long> target = new ConcurrentDoublyLinkedList<>();
        final int end = start + length;
        for (int i = start; i < end; i++)
            target.addLast(source[i]);
        return target;
    }
}
