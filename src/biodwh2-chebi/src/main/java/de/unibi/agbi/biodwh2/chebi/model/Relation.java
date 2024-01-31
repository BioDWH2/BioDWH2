package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "TYPE", "INIT_ID", "FINAL_ID", "STATUS"})
public class Relation {
    @JsonProperty("ID")
    public Integer id;
    @JsonProperty("TYPE")
    public String type;
    @JsonProperty("INIT_ID")
    public Integer initId;
    @JsonProperty("FINAL_ID")
    public Integer finalId;
    @JsonProperty("STATUS")
    public String status;
}
