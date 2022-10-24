package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BiospecRetentionEnum {
    NONE_RETAINED("None Retained"),
    SAMPLES_WITH_DNA("Samples With DNA"),
    SAMPLES_WITHOUT_DNA("Samples Without DNA");

    BiospecRetentionEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
