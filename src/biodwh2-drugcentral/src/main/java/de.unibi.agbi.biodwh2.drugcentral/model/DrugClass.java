package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "name", "is_group", "source"})
@NodeLabels("DrugClass")
public final class DrugClass {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("is_group")
    @GraphBooleanProperty(value = "is_group", truthValue = "1")
    public String isGroup;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
}
