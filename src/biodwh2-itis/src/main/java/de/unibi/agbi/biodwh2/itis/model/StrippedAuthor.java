package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"taxon_author_id", "shortauthor"})
public class StrippedAuthor {
    @JsonProperty("taxon_author_id")
    public int taxonAuthorId;
    @JsonProperty("shortauthor")
    public String shortAuthor;
}
