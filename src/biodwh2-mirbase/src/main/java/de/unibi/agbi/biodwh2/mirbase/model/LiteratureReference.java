package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_lit", "medline", "title", "author", "journal"})
public class LiteratureReference {
    @JsonProperty("auto_lit")
    public Long autoLit;
    @JsonProperty("medline")
    public String medline;
    @JsonProperty("title")
    public String title;
    @JsonProperty("author")
    public String author;
    @JsonProperty("journal")
    public String journal;
}
