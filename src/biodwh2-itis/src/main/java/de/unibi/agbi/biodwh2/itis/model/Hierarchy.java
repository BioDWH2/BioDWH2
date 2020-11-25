package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"hierarchy_string", "TSN", "Parent_TSN", "level", "ChildrenCount"})
public class Hierarchy {
    @JsonProperty("hierarchy_string")
    public String value;
    @JsonProperty("TSN")
    public Integer tsn;
    @JsonProperty("Parent_TSN")
    public Integer parentTsn;
    @JsonProperty("level")
    public Integer level;
    @JsonProperty("ChildrenCount")
    public Integer childrenCount;
}
