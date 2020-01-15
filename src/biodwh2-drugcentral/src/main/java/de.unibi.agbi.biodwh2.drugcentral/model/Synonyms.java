package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"synId", "id", "name", "preferredName", "parentId", "lname"})

public final class Synonyms {
    @JsonProperty("synId")
    public String synId;
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("preferredName")
    public String preferredName;
    @JsonProperty("parentId")
    public String parentId;
    @JsonProperty("lname")
    public String lname;
}
