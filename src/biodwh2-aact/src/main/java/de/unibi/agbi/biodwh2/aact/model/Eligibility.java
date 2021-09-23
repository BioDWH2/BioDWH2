package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "sampling_method", "gender", "minimum_age", "maximum_age", "healthy_volunteers", "population",
        "criteria", "gender_description", "gender_based"
})
public class Eligibility {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("sampling_method")
    public String sampling_method;
    @JsonProperty("gender")
    public String gender;
    @JsonProperty("minimum_age")
    public String minimumAge;
    @JsonProperty("maximum_age")
    public String maximumAge;
    @JsonProperty("healthy_volunteers")
    public String healthyVolunteers;
    @JsonProperty("population")
    public String population;
    @JsonProperty("criteria")
    public String criteria;
    @JsonProperty("gender_description")
    public String genderDescription;
    @JsonProperty("gender_based")
    public String genderBased;
}
