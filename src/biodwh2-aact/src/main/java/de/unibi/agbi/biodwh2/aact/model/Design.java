package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "allocation", "intervention_model", "observational_model", "primary_purpose",
        "time_perspective", "masking", "masking_description", "intervention_model_description", "subject_masked",
        "caregiver_masked", "investigator_masked", "outcomes_assessor_masked"
})
public class Design {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("allocation")
    public String allocation;
    @JsonProperty("intervention_model")
    public String interventionModel;
    @JsonProperty("observational_model")
    public String observationalModel;
    @JsonProperty("primary_purpose")
    public String primaryPurpose;
    @JsonProperty("time_perspective")
    public String timePerspective;
    @JsonProperty("masking")
    public String masking;
    @JsonProperty("masking_description")
    public String maskingDescription;
    @JsonProperty("intervention_model_description")
    public String interventionModelDescription;
    @JsonProperty("subject_masked")
    public String subjectMasked;
    @JsonProperty("caregiver_masked")
    public String caregiverMasked;
    @JsonProperty("investigator_masked")
    public String investigatorMasked;
    @JsonProperty("outcomes_assessor_masked")
    public String outcomesAssessorMasked;
}
