package de.unibi.agbi.biodwh2.hprd.model;

public class Transcript {
    public final String isoformId;
    public String transcriptRefSeqId;
    public String nucleotideSequence;
    public String proteinRefSeqId;
    public String proteinSequence;
    public Integer orfStart;
    public Integer orfEnd;
    public Integer proteinLength;
    public String proteinMolecularWeight;
    public Long transcriptNodeId;
    public Long proteinNodeId;

    public Transcript(final String isoformId) {
        this.isoformId = isoformId;
    }
}
