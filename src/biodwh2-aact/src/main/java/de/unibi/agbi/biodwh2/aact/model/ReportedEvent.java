package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "result_group_id", "ctgov_group_code", "time_frame", "event_type", "default_vocab",
        "default_assessment", "subjects_affected", "subjects_at_risk", "description", "event_count", "organ_system",
        "adverse_event_term", "frequency_threshold", "vocab", "assessment"
})
public class ReportedEvent {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("result_group_id")
    public Long resultGroupId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("time_frame")
    public String timeFrame;
    @JsonProperty("event_type")
    public String eventType;
    @JsonProperty("default_vocab")
    public String defaultVocab;
    @JsonProperty("default_assessment")
    public String defaultAssessment;
    @JsonProperty("subjects_affected")
    public Integer subjectsAffected;
    @JsonProperty("subjects_at_risk")
    public Integer subjectsAtRisk;
    @JsonProperty("description")
    public String description;
    @JsonProperty("event_count")
    public Integer eventCount;
    @JsonProperty("organ_system")
    public String organSystem;
    @JsonProperty("adverse_event_term")
    public String adverseEventTerm;
    @JsonProperty("frequency_threshold")
    public String frequencyThreshold;
    @JsonProperty("vocab")
    public String vocab;
    @JsonProperty("assessment")
    public String assessment;
}
