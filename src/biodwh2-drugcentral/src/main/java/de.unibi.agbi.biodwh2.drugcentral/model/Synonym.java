package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@SuppressWarnings("unused")
@JsonPropertyOrder({"syn_id", "id", "name", "preferred_name", "parent_id", "lname"})
@GraphNodeLabel("Synonyms")
public final class Synonym {
    @JsonProperty("syn_id")
    public String synId;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("preferred_name")
    @GraphProperty("preferred_name")
    public String preferredName;
    @JsonProperty("parent_id")
    public Long parentId;
    @JsonProperty("lname")
    public String lname;
}
