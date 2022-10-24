package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SamplingMethodEnum {
    PROBABILITY_SAMPLE("Probability Sample"),
    NON_PROBABILITY_SAMPLE("Non-Probability Sample");

    SamplingMethodEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
