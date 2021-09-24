package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "ctgov_group_code", "event_type", "classification", "subjects_affected", "subjects_at_risk",
        "created_at", "updated_at"
})
public class ReportedEventTotal {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("event_type")
    public String eventType;
    @JsonProperty("classification")
    public String classification;
    @JsonProperty("subjects_affected")
    public String subjectsAffected;
    @JsonProperty("subjects_at_risk")
    public String subjectsAtRisk;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
}
