package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "pmid", "doi", "document_id", "type", "authors", "title", "isbn10", "url", "journal", "volume", "issue",
        "dp_year", "pages"
})
@GraphNodeLabel("Reference")
public final class Reference {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("pmid")
    @GraphProperty("pmid")
    public String pmid;
    @JsonProperty("doi")
    @GraphProperty("doi")
    public String doi;
    @JsonProperty("document_id")
    @GraphProperty("document_id")
    public String documentId;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("authors")
    @GraphProperty("authors")
    public String authors;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
    @JsonProperty("isbn10")
    @GraphProperty("isbn10")
    public String isbn10;
    @JsonProperty("url")
    @GraphProperty("url")
    public String url;
    @JsonProperty("journal")
    @GraphProperty("journal")
    public String journal;
    @JsonProperty("volume")
    @GraphProperty("volume")
    public String volume;
    @JsonProperty("issue")
    @GraphProperty("issue")
    public String issue;
    @JsonProperty("dp_year")
    @GraphProperty("dp_year")
    public String dpYear;
    @JsonProperty("pages")
    @GraphProperty("pages")
    public String pages;
}
