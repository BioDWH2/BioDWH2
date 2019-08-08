package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Category {
    public String category;
    @JsonProperty("mesh-id")
    public String meshId;
}
