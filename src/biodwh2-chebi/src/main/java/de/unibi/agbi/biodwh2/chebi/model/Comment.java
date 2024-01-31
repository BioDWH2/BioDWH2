package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "COMPOUND_ID", "CREATED_ON", "DATATYPE_ID", "DATATYPE", "TEXT"})
public class Comment {
    @JsonProperty("ID")
    public Integer id;
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("CREATED_ON")
    public String createdOn;
    @JsonProperty("DATATYPE_ID")
    public Integer datatypeId;
    @JsonProperty("DATATYPE")
    public String datatype;
    @JsonProperty("TEXT")
    public String text;
}
