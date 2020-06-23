package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "atc_code", "ddd", "unit_type", "route", "comment", "struct_id"})
@NodeLabels({"AtcDDD"})
public final class AtcDdd {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("atc_code")
    @GraphProperty("atc_code")
    public String atcCode;
    @JsonProperty("ddd")
    @GraphProperty("ddd")
    public String ddd;
    @JsonProperty("unit_type")
    @GraphProperty("unit_type")
    public String unitType;
    @JsonProperty("route")
    @GraphProperty("route")
    public String route;
    @JsonProperty("comment")
    @GraphProperty("comment")
    public String comment;
    @JsonProperty("struct_id")
    public Integer structId;
}
