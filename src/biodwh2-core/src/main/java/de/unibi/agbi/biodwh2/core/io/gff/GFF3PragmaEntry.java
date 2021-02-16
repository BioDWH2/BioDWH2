package de.unibi.agbi.biodwh2.core.io.gff;

public class GFF3PragmaEntry implements GFF3Entry {
    private final String value;

    GFF3PragmaEntry(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GFF3PragmaEntry{" + "value='" + value + '\'' + '}';
    }
}
