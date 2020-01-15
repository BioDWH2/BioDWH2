package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "drugClass1", "drugClass2", "ddiRefId", "ddiRisk", "description", "sourceId"})

public final class Ddi {
    @JsonProperty("id")
    public String id;
    @JsonProperty("drugClass1")
    public String drugClass1;
    @JsonProperty("drugClass2")
    public String drugClass2;
    @JsonProperty("ddiRefId")
    public String ddiRefId;
    @JsonProperty("ddiRisk")
    public String ddiRisk;
    @JsonProperty("description")
    public String description;
    @JsonProperty("sourceId")
    public String sourceId;

}
