package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"hprd_id", "geneSymbol", "refseq_id", "disease_name", "reference_id"})
public class GeneticDisease {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("refseq_id")
    public String refSeqId;
    @JsonProperty("disease_name")
    public String diseaseName;
    @JsonProperty("reference_id")
    public String referenceId;
}
