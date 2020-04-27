package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"id", "struct_id", "pka_level", "value", "pka_type"})
@NodeLabels({"PKA"})
public final class Pka {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("struct_id")
    @GraphProperty("struct_id")
    public String structId;
    @JsonProperty("pka_level")
    @GraphProperty("pka_level")
    public String pkaLevel;
    @JsonProperty("value")
    @GraphProperty("value")
    public String value;
    @JsonProperty("pka_type")
    @GraphProperty("pka_type")
    public String pkaType;
}