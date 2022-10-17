package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_db", "display_name", "url"})
public class MirnaDatabaseUrl {
    @JsonProperty("auto_db")
    public Long autoDb;
    @JsonProperty("display_name")
    public String displayName;
    @JsonProperty("url")
    public String url;
}
