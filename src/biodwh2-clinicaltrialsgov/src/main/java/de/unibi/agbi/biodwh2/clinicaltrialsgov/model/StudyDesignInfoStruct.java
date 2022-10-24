package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudyDesignInfoStruct {
    public String allocation;
    @JsonProperty(value = "intervention_model")
    public String interventionModel;
    @JsonProperty(value = "intervention_model_description")
    public String interventionModelDescription;
    @JsonProperty(value = "primary_purpose")
    public String primaryPurpose;
    @JsonProperty(value = "observational_model")
    public String observationalModel;
    @JsonProperty(value = "time_perspective")
    public String timePerspective;
    public String masking;
    @JsonProperty(value = "masking_description")
    public String maskingDescription;
}
