package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertainAgreementsStruct {
    @JsonProperty(value = "pi_employee")
    public PiEmployeeEnum piEmployee;
    @JsonProperty(value = "restrictive_agreement")
    public String restrictiveAgreement;
}
