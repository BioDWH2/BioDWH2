package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "pmid", "reference_type", "citation"})
public class StudyReference {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("pmid")
    public Integer pmid;
    @JsonProperty("reference_type")
    public String referenceType;
    @JsonProperty("citation")
    public String citation;
}
