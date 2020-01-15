package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "identifier", "idType", "structId", "parentMatch"})

public final class Identifier {
    @JsonProperty("id")
    public String id;
    @JsonProperty("identifier")
    public String identifier;
    @JsonProperty("idType")
    public String idType;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("parentMatch")
    public String parentMatch;
}
