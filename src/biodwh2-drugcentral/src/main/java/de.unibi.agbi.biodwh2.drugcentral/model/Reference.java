package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "pmid", "doi", "documentId", "type", "authors", "title", "isbn10", "url",
"journal", "volume", "issue", "dpYear", "pages"})

public final class Reference {
    @JsonProperty("id")
    public String id;
    @JsonProperty("pmid")
    public String pmid;
    @JsonProperty("doi")
    public String doi;
    @JsonProperty("documentId")
    public String documentId;
    @JsonProperty("type")
    public String type;
    @JsonProperty("authors")
    public String authors;
    @JsonProperty("title")
    public String title;
    @JsonProperty("isbn10")
    public String isbn10;
    @JsonProperty("url")
    public String url;
    @JsonProperty("journal")
    public String journal;
    @JsonProperty("volume")
    public String volume;
    @JsonProperty("issue")
    public String issue;
    @JsonProperty("dpYear")
    public String dpYear;
    @JsonProperty("pages")
    public String pages;
}
