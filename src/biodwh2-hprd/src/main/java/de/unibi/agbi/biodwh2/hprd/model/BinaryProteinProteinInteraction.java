package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "interactor_1_geneSymbol", "interactor_1_hprd_id", "interactor_1_refseq_id", "interactor_2_geneSymbol",
        "interactor_2_hprd_id", "interactor_2_refseq_id", "experiment_type", "reference_id"
})
public class BinaryProteinProteinInteraction {
    @JsonProperty("interactor_1_geneSymbol")
    public String interactor1GeneSymbol;
    @JsonProperty("interactor_1_hprd_id")
    public String interactor1HprdId;
    @JsonProperty("interactor_1_refseq_id")
    public String interactor1RefSeqId;
    @JsonProperty("interactor_2_geneSymbol")
    public String interactor2GeneSymbol;
    @JsonProperty("interactor_2_hprd_id")
    public String interactor2HprdId;
    @JsonProperty("interactor_2_refseq_id")
    public String interactor2RefSeqId;
    @JsonProperty("experiment_type")
    public String experimentType;
    @JsonProperty("reference_id")
    public String referenceId;
}
