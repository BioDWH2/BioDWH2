package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Enzyme extends Interactant {
    @JsonProperty("inhibition-strength")
    public String inhibitionStrength;
    @JsonProperty("induction-strength")
    public String inductionStrength;
}
