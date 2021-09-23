package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "document_id", "document_type", "url", "comment"})
public class Document {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("document_id")
    public String documentId;
    @JsonProperty("document_type")
    public String documentType;
    @JsonProperty("url")
    public String url;
    @JsonProperty("comment")
    public String comment;
}
