package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "interactor_geneSymbol", "interactor_hprd_id", "interactor_refseq_id", "non_protein_interactor_name",
        "experiment_type", "reference_id"
})
public class BinaryProteinNonProteinInteraction {
    @JsonProperty("interactor_geneSymbol")
    public String interactorGeneSymbol;
    @JsonProperty("interactor_hprd_id")
    public String interactorHprdId;
    @JsonProperty("interactor_refseq_id")
    public String interactorRefSeqId;
    @JsonProperty("non_protein_interactor_name")
    public String nonProteinInteractorName;
    @JsonProperty("experiment_type")
    public String experimentType;
    @JsonProperty("reference_id")
    public String referenceId;
}
