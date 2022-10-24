package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SponsorStruct {
    @JsonProperty(required = true)
    public String agency;
    @JsonProperty(value = "agency_class")
    public AgencyClassEnum agencyClass;
}
