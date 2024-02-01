package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "category", "source_db_name", "source_db_version"})
public class Category {
    @JsonProperty("name")
    public String name;
    @JsonProperty("category")
    public String category;
    @JsonProperty("source_db_name")
    public String sourceDBName;
    @JsonProperty("source_db_version")
    public String sourceDBVersion;
}
