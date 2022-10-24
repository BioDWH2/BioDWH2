package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActualEnum {
    ACTUAL("Actual"),
    ANTICIPATED("Anticipated"),
    ESTIMATE("Estimate");

    ActualEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
