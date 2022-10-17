package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_db", "display_name", "url", "type"})
public class MatureDatabaseUrl {
    @JsonProperty("auto_db")
    public Long autoDb;
    @JsonProperty("display_name")
    public String displayName;
    @JsonProperty("url")
    public String url;
    @JsonProperty("type")
    public Integer type;
}
