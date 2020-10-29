package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@JsonPropertyOrder({"kingdom_id", "kingdom_name", "update_date"})
@NodeLabel("Kingdom")
public class Kingdom {
    @JsonProperty("kingdom_id")
    @GraphProperty("id")
    public int id;
    @JsonProperty("kingdom_name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("update_date")
    public String updateDate;
}
