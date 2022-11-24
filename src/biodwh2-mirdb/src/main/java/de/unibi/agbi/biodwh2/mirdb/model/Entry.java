package de.unibi.agbi.biodwh2.mirdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"mirna_id", "genbank_accession", "target_score"})
public class Entry {
    @JsonProperty("mirna_id")
    public String mirnaId;
    @JsonProperty("genbank_accession")
    public String genBankAccession;
    @JsonProperty("target_score")
    public Double targetScore;
}
