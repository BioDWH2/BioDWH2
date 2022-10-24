package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class ReferenceStruct {
    public String citation;
    @JsonProperty(value = "PMID")
    public BigInteger pmid;
}
