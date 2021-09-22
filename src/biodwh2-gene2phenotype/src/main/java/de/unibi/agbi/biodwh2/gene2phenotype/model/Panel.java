package de.unibi.agbi.biodwh2.gene2phenotype.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Panel {
    @JsonProperty("Cancer")
    CANCER("Cancer"),
    @JsonProperty("Cardiac")
    CARDIAC("Cardiac"),
    @JsonProperty("DD")
    DD("DD"),
    @JsonProperty("Ear")
    EAR("Ear"),
    @JsonProperty("Eye")
    EYE("Eye"),
    @JsonProperty("Skin")
    SKIN("Skin");

    private final String value;

    Panel(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
