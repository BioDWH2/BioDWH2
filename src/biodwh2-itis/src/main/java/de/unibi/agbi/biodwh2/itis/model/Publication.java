package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@JsonPropertyOrder({
        "pub_id_prefix", "publication_id", "reference_author", "title", "publication_name", "listed_pub_date",
        "actual_pub_date", "publisher", "pub_place", "isbn", "issn", "pages", "pub_comment", "update_date"
})
@GraphNodeLabel("Publication")
public class Publication {
    @JsonProperty("pub_id_prefix")
    public String idPrefix;
    @JsonProperty("publication_id")
    @GraphProperty("id")
    public int id;
    @JsonProperty("reference_author")
    @GraphProperty("authors")
    public String referenceAuthor;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
    @JsonProperty("publication_name")
    @GraphProperty("journal_or_book_name")
    public String name;
    @JsonProperty("listed_pub_date")
    public String listedPubDate;
    @JsonProperty("actual_pub_date")
    @GraphProperty("publication_date")
    public String actualPubDate;
    @JsonProperty("publisher")
    @GraphProperty("publisher")
    public String publisher;
    @JsonProperty("pub_place")
    @GraphProperty("publication_place")
    public String place;
    @JsonProperty("isbn")
    @GraphProperty("isbn")
    public String isbn;
    @JsonProperty("issn")
    @GraphProperty("issn")
    public String issn;
    @JsonProperty("pages")
    @GraphProperty("pages")
    public String pages;
    @JsonProperty("pub_comment")
    @GraphProperty("comments")
    public String comment;
    @JsonProperty("update_date")
    public String updateDate;
}
