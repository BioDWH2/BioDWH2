package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EligibilityStruct {
    @JsonProperty(value = "study_pop")
    public TextblockStruct studyPop;
    @JsonProperty(value = "sampling_method")
    public SamplingMethodEnum samplingMethod;
    public TextblockStruct criteria;
    @JsonProperty(required = true)
    public GenderEnum gender;
    @JsonProperty(value = "gender_based")
    public YesNoEnum genderBased;
    @JsonProperty(value = "gender_description")
    public String genderDescription;
    @JsonProperty(value = "minimum_age", required = true)
    public String minimumAge;
    @JsonProperty(value = "maximum_age", required = true)
    public String maximumAge;
    @JsonProperty(value = "healthy_volunteers")
    public String healthyVolunteers;
}
