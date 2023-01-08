package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Organism {
    @JsonProperty("value")
    public String value;
    @JsonProperty("comment")
    public String comment;
}
