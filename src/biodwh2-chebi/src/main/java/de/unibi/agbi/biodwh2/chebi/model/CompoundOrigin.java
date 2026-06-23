package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "id", "compound_id", "species_source_id", "species_text", "species_accession", "component_source_id",
        "component_text", "component_accession", "strain_text", "source_id", "source_accession", "comments", "status_id"
})
public class CompoundOrigin {
    @JsonProperty("id")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("species_source_id")
    public Integer speciesSourceId;
    @JsonProperty("species_text")
    public String speciesText;
    @JsonProperty("species_accession")
    public String speciesAccession;
    @JsonProperty("component_source_id")
    public Integer componentSourceId;
    @JsonProperty("component_text")
    @GraphProperty(value = "component_text", emptyPlaceholder = "null")
    public String componentText;
    @JsonProperty("component_accession")
    @GraphProperty(value = "component_accession", emptyPlaceholder = "null")
    public String componentAccession;
    @JsonProperty("strain_text")
    @GraphProperty(value = "strain_text", emptyPlaceholder = "null")
    public String strainText;
    @JsonProperty("source_id")
    public Integer sourceId;
    @JsonProperty("STRAIN_ACCESSION")
    @GraphProperty(value = "strain_accession", emptyPlaceholder = "null")
    public String strainAccession;
    @JsonProperty("source_accession")
    @GraphProperty(value = "source_accession", emptyPlaceholder = "null")
    public String sourceAccession;
    @JsonProperty("comments")
    @GraphProperty(value = "comments", emptyPlaceholder = "null")
    public String comments;
    @JsonProperty("status_id")
    public Integer statusId;
}
