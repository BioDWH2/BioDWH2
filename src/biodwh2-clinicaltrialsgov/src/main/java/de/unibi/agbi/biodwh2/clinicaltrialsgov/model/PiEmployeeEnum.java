package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PiEmployeeEnum {
    ALL_PRINCIPAL_INVESTIGATORS_ARE_EMPLOYED_BY_THE_ORGANIZATION_SPONSORING_THE_STUDY(
            "All Principal Investigators ARE employed by the organization sponsoring the study."),
    PRINCIPAL_INVESTIGATORS_ARE_NOT_EMPLOYED_BY_THE_ORGANIZATION_SPONSORING_THE_STUDY(
            "Principal Investigators are NOT employed by the organization sponsoring the study.");

    PiEmployeeEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
