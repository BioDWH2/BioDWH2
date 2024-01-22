package de.unibi.agbi.biodwh2.tarbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "species", "mirna_name", "mirna_id", "gene_name", "gene_id", "gene_location", "transcript_name",
        "transcript_id", "chromosome", "start", "end", "strand", "experimental_method", "regulation", "tissue",
        "cell_line", "article_pubmed_id", "confidence", "interaction_group", "cell_type", "microt_score", "comment"
})
public class Entry {
    @JsonProperty("species")
    public String species;
    @JsonProperty("mirna_name")
    public String mirnaName;
    @JsonProperty("mirna_id")
    public String mirnaId;
    @JsonProperty("gene_name")
    public String geneName;
    @JsonProperty("gene_id")
    public String geneId;
    /**
     * The region inside the gene body in which the miRNA binds (e.g., 3UTR or CDS).
     */
    @JsonProperty("gene_location")
    public String geneLocation;
    @JsonProperty("transcript_name")
    public String transcriptName;
    @JsonProperty("transcript_id")
    public String transcriptId;
    @JsonProperty("chromosome")
    public String chromosome;
    @JsonProperty("start")
    public String start;
    @JsonProperty("end")
    public String end;
    @JsonProperty("strand")
    public String strand;
    @JsonProperty("experimental_method")
    public String experimentalMethod;
    /**
     * The regulation type of the interaction (i.e., whether the miRNA down- or up-regulates the target gene – Negative
     * means downregulation while Positive the opposite).
     */
    @JsonProperty("regulation")
    public String regulation;
    @JsonProperty("tissue")
    public String tissue;
    @JsonProperty("cell_line")
    public String cellLine;
    @JsonProperty("article_pubmed_id")
    public String articlePubmedId;
    @JsonProperty("confidence")
    public String confidence;
    @JsonProperty("interaction_group")
    public String interactionGroup;
    @JsonProperty("cell_type")
    public String cellType;
    /**
     * The predicted interaction score of microT-CDS-2023 for the same, experimentally supported interaction. The score
     * of microT-CDS-2023 is always between 0 and 1.
     */
    @JsonProperty("microt_score")
    public String microtScore;
    @JsonProperty("comment")
    public String comment;
}
