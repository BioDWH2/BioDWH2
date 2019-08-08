package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Group {
    Approved("approved"),
    Illicit("illicit"),
    Experimental("experimental"),
    Withdrawn("withdrawn"),
    Nutraceutical("nutraceutical"),
    Investigational("investigational"),
    Vet_Approved("vet_approved");

    private Group(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
