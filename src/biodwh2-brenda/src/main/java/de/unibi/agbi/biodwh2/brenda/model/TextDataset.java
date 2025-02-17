package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextDataset extends Dataset {
    @JsonProperty("value")
    public String value;
}
