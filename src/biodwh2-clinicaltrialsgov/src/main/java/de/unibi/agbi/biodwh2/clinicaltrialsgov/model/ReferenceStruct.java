package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReferenceStruct {
    public String citation;
    @JsonProperty(value = "PMID")
    public Integer pmid;
}
