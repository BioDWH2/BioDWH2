package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"syn_id", "id", "name", "preferred_name", "parent_id", "lname"})
@NodeLabels({"Synonyms"})
public final class Synonyms {
    @JsonProperty("syn_id")
    @GraphProperty("syn_id")
    public String synId;
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("preferred_name")
    @GraphProperty("preferred_name")
    public String preferredName;
    @JsonProperty("parent_id")
    @GraphProperty("parent_id")
    public String parentId;
    @JsonProperty("lname")
    @GraphProperty("lname")
    public String lname;
}
