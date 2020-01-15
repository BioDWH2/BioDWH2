package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "actCode", "ddd", "unitType", "route", "comment", "structId"})

public final class ActDdd {
    @JsonProperty("id")
    public String id;
    @JsonProperty("actCode")
    public String actCode;
    @JsonProperty("ddd")
    public String ddd;
    @JsonProperty("unitType")
    public String unitType;
    @JsonProperty("route")
    public String route;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("structId")
    public String structId;
}
