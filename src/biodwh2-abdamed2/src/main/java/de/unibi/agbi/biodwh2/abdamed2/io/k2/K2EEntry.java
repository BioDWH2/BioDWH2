package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import java.util.List;

/**
 * K2 format "End" entry
 */
public class K2EEntry extends K2Entry {
    private long numberOfDBlocks;
    private long numberOfIBlocks;
    private long numberOfUBlocks;
    private long numberOfAllFields;

    K2EEntry(final EntryType type) {
        super(type);
    }

    @Override
    protected void parse(final String[] lines, final List<K2FEntry> fields) {
        numberOfDBlocks = Long.parseLong(lines[0]);
        numberOfIBlocks = Long.parseLong(lines[1]);
        numberOfUBlocks = Long.parseLong(lines[2]);
        numberOfAllFields = Long.parseLong(lines[3]);
    }

    public long getNumberOfDBlocks() {
        return numberOfDBlocks;
    }

    public long getNumberOfIBlocks() {
        return numberOfIBlocks;
    }

    public long getNumberOfUBlocks() {
        return numberOfUBlocks;
    }

    public long getNumberOfAllFields() {
        return numberOfAllFields;
    }
}
