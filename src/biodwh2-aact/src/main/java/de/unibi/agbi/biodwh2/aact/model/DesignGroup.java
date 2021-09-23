package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "group_type", "title", "description"})
public class DesignGroup {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("group_type")
    public String groupType;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
}
