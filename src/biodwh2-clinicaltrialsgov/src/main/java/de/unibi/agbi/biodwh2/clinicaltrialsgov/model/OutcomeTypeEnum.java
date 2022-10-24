package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OutcomeTypeEnum {
    PRIMARY("Primary"),
    SECONDARY("Secondary"),
    OTHER_PRE_SPECIFIED("Other Pre-specified"),
    POST_HOC("Post-Hoc");

    OutcomeTypeEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
