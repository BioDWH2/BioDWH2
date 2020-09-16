package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@JsonPropertyOrder({
        "pub_id_prefix", "publication_id", "reference_author", "title", "publication_name", "listed_pub_date",
        "actual_pub_date", "publisher", "pub_place", "isbn", "issn", "pages", "pub_comment", "update_date"
})
public class Publication {
    @JsonProperty("pub_id_prefix")
    public String idPrefix;
    @JsonProperty("publication_id")
    public int id;
    @JsonProperty("reference_author")
    public String referenceAuthor;
    @JsonProperty("title")
    public String title;
    @JsonProperty("publication_name")
    public String name;
    @JsonProperty("listed_pub_date")
    public String listedPubDate;
    @JsonProperty("actual_pub_date")
    public String actualPubDate;
    @JsonProperty("publisher")
    public String publisher;
    @JsonProperty("pub_place")
    public String place;
    @JsonProperty("isbn")
    public String isbn;
    @JsonProperty("issn")
    public String issn;
    @JsonProperty("pages")
    public String pages;
    @JsonProperty("pub_comment")
    public String comment;
    @JsonProperty("update_date")
    public String updateDate;
}
