package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_mirna", "auto_db", "link", "display_name"})
public class MirnaDatabaseLink {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("auto_db")
    public Long autoDb;
    @JsonProperty("link")
    public String link;
    @JsonProperty("display_name")
    public String displayName;
}
