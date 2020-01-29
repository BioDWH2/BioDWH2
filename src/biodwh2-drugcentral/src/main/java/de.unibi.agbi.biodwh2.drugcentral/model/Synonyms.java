package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"syn_id", "id", "name", "preferred_name", "parent_id", "lname"})

public final class Synonyms {
    @JsonProperty("syn_id")
    public String synId;
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("preferred_name")
    public String preferredName;
    @JsonProperty("parent_id")
    public String parentId;
    @JsonProperty("lname")
    public String lname;
}
