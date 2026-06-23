package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "relation_type_id", "init_id", "final_id", "status_id", "evidence_accession", "evidence_source_id"
})
public class Relation {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("relation_type_id")
    public Integer relationTypeId;
    @JsonProperty("init_id")
    public Integer initId;
    @JsonProperty("final_id")
    public Integer finalId;
    @JsonProperty("status_id")
    public Integer statusId;
    @JsonProperty("evidence_accession")
    public String evidenceAccession;
    @JsonProperty("evidence_source_id")
    public Integer evidenceSourceId;
}
