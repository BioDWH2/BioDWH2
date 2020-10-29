package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"hierarchy_string", "TSN", "Parent_TSN", "level", "ChildrenCount"})
public class Hierarchy {
    @JsonProperty("hierarchy_string")
    public String value;
    @JsonProperty("TSN")
    public int tsn;
    @JsonProperty("Parent_TSN")
    public int parentTsn;
    @JsonProperty("level")
    public int level;
    @JsonProperty("ChildrenCount")
    public int childrenCount;
}
