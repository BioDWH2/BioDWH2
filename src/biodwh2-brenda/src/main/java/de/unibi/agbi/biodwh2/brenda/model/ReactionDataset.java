package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReactionDataset extends Dataset {
    @JsonProperty("educts")
    public String[] educts;
    @JsonProperty("products")
    public String[] products;
}
