package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "hprd_id", "isoform_id", "refseq_id", "geneSymbol", "isoform_specifity_status", "molecular_function_term",
        "molecular_function_reference_id", "biological_process_term", "biological_process_reference_id",
        "cellular_component_term", "cellular_component_reference_id"
})
public class GeneOntology {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("isoform_id")
    public String isoformId;
    @JsonProperty("refseq_id")
    public String refSeqId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("isoform_specifity_status")
    public String isoformSpecifityStatus;
    @JsonProperty("molecular_function_term")
    public String molecularFunctionTerm;
    @JsonProperty("molecular_function_reference_id")
    public String molecularFunctionReferenceId;
    @JsonProperty("biological_process_term")
    public String biologicalProcessTerm;
    @JsonProperty("biological_process_reference_id")
    public String biologicalProcessReferenceId;
    @JsonProperty("cellular_component_term")
    public String cellularComponentTerm;
    @JsonProperty("cellular_component_reference_id")
    public String cellularComponentReferenceId;
}
