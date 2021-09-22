package de.unibi.agbi.biodwh2.gene2phenotype.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DiseaseConfidence {
    @JsonProperty("both RD and IF")
    RD_IF("both RD and IF"),
    @JsonProperty("confirmed")
    CONFIRMED("confirmed"),
    @JsonProperty("possible")
    POSSIBLE("possible"),
    @JsonProperty("probable")
    PROBABLE("probable"),
    @JsonProperty("child IF")
    CHILD_IF("child IF"),
    @JsonProperty("definitive")
    DEFINITIVE("definitive"),
    @JsonProperty("strong")
    STRONG("strong"),
    @JsonProperty("limited")
    LIMITED("limited");

    private final String value;

    DiseaseConfidence(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
