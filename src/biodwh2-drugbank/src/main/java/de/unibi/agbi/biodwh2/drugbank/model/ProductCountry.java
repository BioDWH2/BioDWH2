package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCountry {
    US("US"),
    Canada("Canada"),
    EU("EU");

    ProductCountry(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
