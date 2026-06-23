package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "id", "name", "status_id", "source", "parent_id", "merge_type", "chebi_accession", "definition", "ascii_name",
        "stars", "modified_on", "release_date"
})
@GraphNodeLabel(ChEBIGraphExporter.COMPOUND_LABEL)
public class Compound {
    @JsonProperty("id")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("name")
    @GraphProperty(value = "name", emptyPlaceholder = "null")
    public String name;
    @JsonProperty("status_id")
    public Integer statusId;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
    @JsonProperty("parent_id")
    public Integer parentId;
    @JsonProperty("merge_type")
    @GraphProperty("merge_type")
    public String mergeType;
    @JsonProperty("chebi_accession")
    @GraphProperty("accession")
    public String chebiAccession;
    @JsonProperty("definition")
    @GraphProperty(value = "definition", emptyPlaceholder = "null")
    public String definition;
    @JsonProperty("ascii_name")
    @GraphProperty(value = "ascii_name", emptyPlaceholder = "null")
    public String asciiName;
    @JsonProperty("stars")
    @GraphProperty("stars")
    public Integer stars;
    @JsonProperty("modified_on")
    @GraphProperty(value = "modified_on", emptyPlaceholder = "null")
    public String modifiedOn;
    @JsonProperty("release_date")
    @GraphProperty(value = "release_date", emptyPlaceholder = "null")
    public String releaseDate;
}
