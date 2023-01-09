package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "hprd_interaction_id", "interactor_hprd_id", "interactor_geneSymbol", "interactor_refseq_id", "experiment_type",
        "reference_id"
})
public class ProteinComplex {
    @JsonProperty("hprd_interaction_id")
    public String hprdInteractionId;
    @JsonProperty("interactor_hprd_id")
    public String interactorHprdId;
    @JsonProperty("interactor_geneSymbol")
    public String interactorGeneSymbol;
    @JsonProperty("interactor_refseq_id")
    public String interactorRefSeqId;
    @JsonProperty("experiment_type")
    public String experimentType;
    @JsonProperty("reference_id")
    public String referenceId;
}
