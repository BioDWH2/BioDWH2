package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RedactedRecordStatusEnum {
    WITHHELD("Withheld");

    RedactedRecordStatusEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
