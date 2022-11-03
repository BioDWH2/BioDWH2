package de.unibi.agbi.biodwh2.core.collections;

import java.lang.reflect.Array;
import java.util.*;

public final class CollectionUtils {
    public static <T> T[] toArray(Iterable<T> iterable, Class<T> type) {
        final List<T> result = new ArrayList<>();
        for (final T entry : iterable)
            result.add(entry);
        //noinspection unchecked
        return result.toArray((T[]) Array.newInstance(type, result.size()));
    }

    public static <T> List<T> toList(Iterable<T> iterable) {
        final List<T> result = new ArrayList<>();
        for (final T entry : iterable)
            result.add(entry);
        return result;
    }

    public static <T> Set<T> toSet(Iterable<T> iterable) {
        final Set<T> result = new HashSet<>();
        for (final T entry : iterable)
            result.add(entry);
        return result;
    }

    public static <T> T[] toArray(Iterator<T> iterator, Class<T> type) {
        final List<T> result = new ArrayList<>();
        while (iterator.hasNext())
            result.add(iterator.next());
        //noinspection unchecked
        return result.toArray((T[]) Array.newInstance(type, result.size()));
    }

    public static <T> List<T> toList(Iterator<T> iterator) {
        final List<T> result = new ArrayList<>();
        while (iterator.hasNext())
            result.add(iterator.next());
        return result;
    }

    public static <T> Set<T> toSet(Iterator<T> iterator) {
        final Set<T> result = new HashSet<>();
        while (iterator.hasNext())
            result.add(iterator.next());
        return result;
    }
}
