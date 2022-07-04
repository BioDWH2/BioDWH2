package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"database_id", "name", "url", "specialist", "prefix"})
@GraphNodeLabel("Database")
public class Database {
    @JsonProperty("database_id")
    @GraphProperty("id")
    public Long databaseId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("url")
    @GraphProperty("url")
    public String url;
    @JsonProperty("specialist")
    @GraphBooleanProperty(value = "specialist", truthValue = "t")
    public String specialist;
    @JsonProperty("prefix")
    @GraphProperty("prefix")
    public String prefix;
}
