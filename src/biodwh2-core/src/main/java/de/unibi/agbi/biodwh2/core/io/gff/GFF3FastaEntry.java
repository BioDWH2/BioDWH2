package de.unibi.agbi.biodwh2.core.io.gff;

public class GFF3FastaEntry implements GFF3Entry {
    private final String tag;
    private final String sequence;


    public GFF3FastaEntry(final String tag, final String sequence) {
        this.tag = tag;
        this.sequence = sequence;
    }

    public String getTag() {
        return tag;
    }

    public String getSequence() {
        return sequence;
    }
}
