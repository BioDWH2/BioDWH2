package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InterventionTypeEnum {
    BEHAVIORAL("Behavioral"),
    BIOLOGICAL("Biological"),
    COMBINATION_PRODUCT("Combination Product"),
    DEVICE("Device"),
    DIAGNOSTIC_TEST("Diagnostic Test"),
    DIETARY_SUPPLEMENT("Dietary Supplement"),
    DRUG("Drug"),
    GENETIC("Genetic"),
    PROCEDURE("Procedure"),
    RADIATION("Radiation"),
    OTHER("Other");

    InterventionTypeEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
