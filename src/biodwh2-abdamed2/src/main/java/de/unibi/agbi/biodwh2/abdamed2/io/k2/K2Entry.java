package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import java.util.List;

public abstract class K2Entry {
    private final EntryType type;

    protected K2Entry(final EntryType type) {
        this.type = type;
    }

    public EntryType getType() {
        return type;
    }

    public static K2Entry fromType(final String typeKey) {
        final EntryType type;
        try {
            type = EntryType.valueOf(typeKey);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        switch (type) {
            case K:
                return new K2KEntry(type);
            case F:
                return new K2FEntry(type);
            case D:
                return new K2DEntry(type);
            case I:
                return new K2IEntry(type);
            case U:
                return new K2UEntry(type);
            case E:
                return new K2EEntry(type);
        }
        return null;
    }

    protected abstract void parse(final String[] lines, final List<K2FEntry> fields);
}
