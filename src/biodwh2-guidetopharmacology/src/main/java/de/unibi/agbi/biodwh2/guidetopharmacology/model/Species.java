package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "species_id", "name", "short_name", "scientific_name", "ncbi_taxonomy_id", "comments", "description",
        "name_vector", "short_name_vector"
})
@GraphNodeLabel("Species")
public class Species {
    @JsonProperty("species_id")
    @GraphProperty("id")
    public Long speciesId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("short_name")
    @GraphProperty("short_name")
    public String shortSame;
    @JsonProperty("scientific_name")
    @GraphProperty("scientific_name")
    public String scientificName;
    @JsonProperty("ncbi_taxonomy_id")
    @GraphProperty("ncbi_taxonomy_id")
    public Long ncbiTaxonomyId;
    @JsonProperty("comments")
    @GraphProperty("comments")
    public String comments;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("name_vector")
    public String nameVector;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("short_name_vector")
    public String shortNameVector;
}
