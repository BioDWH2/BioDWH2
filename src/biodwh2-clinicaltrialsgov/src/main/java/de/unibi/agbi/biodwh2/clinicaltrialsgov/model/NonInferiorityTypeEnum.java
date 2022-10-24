package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NonInferiorityTypeEnum {
    SUPERIORITY("Superiority"),
    NON_INFERIORITY("Non-Inferiority"),
    EQUIVALENCE("Equivalence"),
    OTHER("Other"),
    NON_INFERIORITY_OR_EQUIVALENCE("Non-Inferiority or Equivalence"),
    NON_INFERIORITY_OR_EQUIVALENCE_LEGACY("Non-Inferiority or Equivalence (legacy)"),
    SUPERIORITY_OR_OTHER("Superiority or Other"),
    SUPERIORITY_OR_OTHER_LEGACY("Superiority or Other (legacy)");

    NonInferiorityTypeEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
