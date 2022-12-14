package de.unibi.agbi.biodwh2.core.io.gff;

import java.util.Map;

public class GFF3DataEntry implements GFF3Entry {
    public enum Strand {
        POSITIVE('+'),
        NEGATIVE('-'),
        UNSTRANDED(null),
        RELEVANT_BUT_UNKNOWN('?'),
        UNKNOWN_VALUE(null);

        public final Character value;

        Strand(Character value) {
            this.value = value;
        }
    }

    private final String seqId;
    private final String source;
    private final String typeSOId;
    private final String typeSOName;
    private final Long start;
    private final Long end;
    private final Double score;
    private final Strand strand;
    private final Integer phase;
    private final Map<String, String> attributes;

    GFF3DataEntry(final String seqId, final String source, final String typeSOId, final String typeSOName,
                  final Long start, final Long end, final Double score, final Strand strand, final Integer phase,
                  final Map<String, String> attributes) {
        this.seqId = seqId;
        this.source = source;
        this.typeSOId = typeSOId;
        this.typeSOName = typeSOName;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.phase = phase;
        this.attributes = attributes;
    }

    /**
     * The ID of the landmark used to establish the coordinate system for the current feature.
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * The source is a free text qualifier intended to describe the algorithm or operating procedure that generated this
     * feature.
     */
    public String getSource() {
        return source;
    }

    /**
     * The type of the feature (previously called the "method"). If provided this returns the Sequence Ontology (SO)
     * accession number.
     */
    public String getTypeSOId() {
        return typeSOId;
    }

    /**
     * The type of the feature (previously called the "method"). If provided this returns the Sequence Ontology (SO)
     * term.
     */
    public String getTypeSOName() {
        return typeSOName;
    }

    /**
     * The start coordinate of the feature given in positive 1-based integer coordinates, relative to the landmark.
     */
    public Long getStart() {
        return start;
    }

    /**
     * The end coordinate of the feature given in positive 1-based integer coordinates, relative to the landmark.
     */
    public Long getEnd() {
        return end;
    }

    /**
     * The score of the feature. It is strongly recommended that E-values be used for sequence similarity features, and
     * that P-values be used for ab initio gene prediction features.
     */
    public Double getScore() {
        return score;
    }

    /**
     * The strand of the feature. + for positive strand (relative to the landmark), - for minus strand, and . for
     * features that are not stranded. In addition, ? can be used for features whose strandedness is relevant, but
     * unknown.
     */
    public Strand getStrand() {
        return strand;
    }

    /**
     * For features of type "CDS", the phase indicates where the next codon begins relative to the 5' end of the current
     * CDS feature.
     */
    public Integer getPhase() {
        return phase;
    }

    public Iterable<String> getAttributeKeys() {
        return attributes.keySet();
    }

    public boolean hasAttribute(final String key) {
        return attributes.containsKey(key);
    }

    public String getAttribute(final String key) {
        return attributes.get(key);
    }

    @Override
    public String toString() {
        return "GFF3DataEntry{" + "seqId='" + seqId + "', source='" + source + "', typeSOId='" + typeSOId +
               "', typeSOName='" + typeSOName + "', start=" + start + ", end=" + end + ", score=" + score +
               ", strand=" + strand + ", phase=" + phase + ", attributes=" + attributes + '}';
    }
}
