package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CalculatedPropertySource {
    ChemAxon("ChemAxon"),
    ALOGPS("ALOGPS");

    private CalculatedPropertySource(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
