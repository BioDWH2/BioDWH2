package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventAssessmentEnum {
    NON_SYSTEMATIC_ASSESSMENT("Non-systematic Assessment"),
    SYSTEMATIC_ASSESSMENT("Systematic Assessment");

    EventAssessmentEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
