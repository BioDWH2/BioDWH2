package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "pi_employee", "agreement", "restriction_type", "other_details", "restrictive_agreement"
})
public class ResultAgreement {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("pi_employee")
    public String piEmployee;
    @JsonProperty("agreement")
    public String agreement;
    @JsonProperty("restriction_type")
    public String restrictionType;
    @JsonProperty("other_details")
    public String otherDetails;
    @JsonProperty("restrictive_agreement")
    public String restrictiveAgreement;
}
