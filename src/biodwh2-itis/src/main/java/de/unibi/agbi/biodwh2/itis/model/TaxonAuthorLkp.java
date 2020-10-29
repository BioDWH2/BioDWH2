package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"taxon_author_id", "taxon_author", "update_date", "kingdom_id", "short_author"})
@NodeLabel("TaxonAuthor")
public class TaxonAuthorLkp {
    @JsonProperty("taxon_author_id")
    @GraphProperty("id")
    public int id;
    @JsonProperty("taxon_author")
    @GraphProperty("name")
    public String taxonAuthor;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("kingdom_id")
    public int kingdomId;
    @JsonProperty("short_author")
    @GraphProperty("short_name")
    public String shortAuthor;
}
