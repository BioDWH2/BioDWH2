package de.unibi.agbi.biodwh2.sider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder({"id", "name"})
@NodeLabels("Drug")
public class DrugName {
    @JsonProperty("id")
    @GraphProperty("flat_id")
    public String id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
}
