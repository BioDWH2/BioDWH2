package de.unibi.agbi.biodwh2.core.io.fasta;

public class FastaEntry {
    private String header;
    private String sequence;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
