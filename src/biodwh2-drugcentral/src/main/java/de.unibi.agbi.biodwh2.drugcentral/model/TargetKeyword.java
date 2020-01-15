package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "descr", "category", "keyword"})

public final class TargetKeyword {
    @JsonProperty("id")
    public String id;
    @JsonProperty("descr")
    public String descr;
    @JsonProperty("category")
    public String category;
    @JsonProperty("keyword")
    public String keyword;
}
