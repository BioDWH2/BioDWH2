package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "compound_id", "comment", "author_name", "status_id", "datatype", "datatype_id"})
public class Comment {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("author_name")
    public String authorName;
    @JsonProperty("status_id")
    public Integer statusId;
    @JsonProperty("datatype")
    public String datatype;
    @JsonProperty("datatype_id")
    public Integer datatypeId;
}
