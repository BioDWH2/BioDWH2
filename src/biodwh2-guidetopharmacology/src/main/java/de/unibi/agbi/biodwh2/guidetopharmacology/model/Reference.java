package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "reference_id", "type", "title", "article_title", "year", "issue", "volume", "pages", "publisher",
        "publisher_address", "editors", "pubmed_id", "isbn", "pub_status", "topics", "comments", "read", "useful",
        "website", "url", "doi", "accessed", "modified", "patent_number", "priority", "publication", "authors",
        "assignee", "pmc_id", "authors_vector", "article_title_vector"
})
@GraphNodeLabel("Reference")
public class Reference {
    @JsonProperty("reference_id")
    @GraphProperty("id")
    public Long referenceId;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
    @JsonProperty("article_title")
    @GraphProperty("article_title")
    public String articleTitle;
    @JsonProperty("year")
    @GraphProperty("year")
    public String year;
    @JsonProperty("issue")
    @GraphProperty("issue")
    public String issue;
    @JsonProperty("volume")
    @GraphProperty("volume")
    public String volume;
    @JsonProperty("pages")
    @GraphProperty("pages")
    public String pages;
    @JsonProperty("publisher")
    @GraphProperty("publisher")
    public String publisher;
    @JsonProperty("publisher_address")
    @GraphProperty("publisher_address")
    public String publisherAddress;
    @JsonProperty("editors")
    @GraphProperty("editors")
    public String editors;
    @JsonProperty("pubmed_id")
    @GraphProperty("pubmed_id")
    public String pubmedId;
    @JsonProperty("isbn")
    @GraphProperty("isbn")
    public String isbn;
    @JsonProperty("pub_status")
    @GraphProperty("pub_status")
    public String pubStatus;
    @JsonProperty("topics")
    @GraphProperty("topics")
    public String topics;
    @JsonProperty("comments")
    @GraphProperty("comments")
    public String comments;
    @JsonProperty("read")
    @GraphProperty("read")
    public String read;
    @JsonProperty("useful")
    @GraphProperty("useful")
    public String useful;
    @JsonProperty("website")
    @GraphProperty("website")
    public String website;
    @JsonProperty("url")
    @GraphProperty("url")
    public String url;
    @JsonProperty("doi")
    @GraphProperty("doi")
    public String doi;
    @JsonProperty("accessed")
    @GraphProperty("accessed")
    public String accessed;
    @JsonProperty("modified")
    @GraphProperty("modified")
    public String modified;
    @JsonProperty("patent_number")
    @GraphProperty("patent_number")
    public String patentNumber;
    @JsonProperty("priority")
    @GraphProperty("priority")
    public String priority;
    @JsonProperty("publication")
    @GraphProperty("publication")
    public String publication;
    @JsonProperty("authors")
    @GraphProperty("authors")
    public String authors;
    @JsonProperty("assignee")
    @GraphProperty("assignee")
    public String assignee;
    @JsonProperty("pmc_id")
    @GraphProperty("pmc_id")
    public String pmcId;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("authors_vector")
    public String authorsVector;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("article_title_vector")
    public String articleTitleVector;
}
