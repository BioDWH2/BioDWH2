package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "type", "name", "classCode", "source"})
public final class PharmaClass {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("type")
    public String type;
    @JsonProperty("name")
    public String name;
    @JsonProperty("classCode")
    public String classCode;
    @JsonProperty("source")
    public String source;
}
