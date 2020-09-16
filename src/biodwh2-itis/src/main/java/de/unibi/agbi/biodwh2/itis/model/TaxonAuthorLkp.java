package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"taxon_author_id", "taxon_author", "update_date", "kingdom_id", "short_author"})
public class TaxonAuthorLkp {
    @JsonProperty("taxon_author_id")
    public int id;
    @JsonProperty("taxon_author")
    public String taxonAuthor;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("kingdom_id")
    public int kingdomId;
    @JsonProperty("short_author")
    public String shortAuthor;
}
