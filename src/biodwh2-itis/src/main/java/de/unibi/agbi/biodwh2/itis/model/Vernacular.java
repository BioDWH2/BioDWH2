package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "vernacular_name", "language", "approved_ind", "update_date", "vern_id"})
@NodeLabel("Vernacular")
public class Vernacular {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("vernacular_name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("language")
    @GraphProperty("language")
    public String language;
    @JsonProperty("approved_ind")
    @GraphProperty("approved")
    public String approved;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("vern_id")
    @GraphProperty("id")
    public int vernacularId;
}
