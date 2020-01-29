package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "act_code", "ddd", "unit_type", "route", "comment", "struct_id"})

public final class AtcDdd {
    @JsonProperty("id")
    public String id;
    @JsonProperty("act_code")
    public String actCode;
    @JsonProperty("ddd")
    public String ddd;
    @JsonProperty("unit_type")
    public String unitType;
    @JsonProperty("route")
    public String route;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("struct_id")
    public String structId;
}
