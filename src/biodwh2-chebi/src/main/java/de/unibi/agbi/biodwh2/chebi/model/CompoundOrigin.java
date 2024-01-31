package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "ID", "COMPOUND_ID", "SPECIES_TEXT", "SPECIES_ACCESSION", "COMPONENT_TEXT", "COMPONENT_ACCESSION",
        "STRAIN_TEXT", "STRAIN_ACCESSION", "SOURCE_TYPE", "SOURCE_ACCESSION", "COMMENTS"
})
@GraphNodeLabel(ChEBIGraphExporter.ORIGIN_LABEL)
public class CompoundOrigin {
    @JsonProperty("ID")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("SPECIES_TEXT")
    @GraphProperty(value = "species_text", emptyPlaceholder = "null")
    public String speciesText;
    @JsonProperty("SPECIES_ACCESSION")
    @GraphProperty(value = "species_accession", emptyPlaceholder = "null")
    public String speciesAccession;
    @JsonProperty("COMPONENT_TEXT")
    @GraphProperty(value = "component_text", emptyPlaceholder = "null")
    public String componentText;
    @JsonProperty("COMPONENT_ACCESSION")
    @GraphProperty(value = "component_accession", emptyPlaceholder = "null")
    public String componentAccession;
    @JsonProperty("STRAIN_TEXT")
    @GraphProperty(value = "strain_text", emptyPlaceholder = "null")
    public String strainText;
    @JsonProperty("STRAIN_ACCESSION")
    @GraphProperty(value = "strain_accession", emptyPlaceholder = "null")
    public String strainAccession;
    @JsonProperty("SOURCE_TYPE")
    @GraphProperty(value = "source_type", emptyPlaceholder = "null")
    public String sourceType;
    @JsonProperty("SOURCE_ACCESSION")
    @GraphProperty(value = "source_accession", emptyPlaceholder = "null")
    public String sourceAccession;
    @JsonProperty("COMMENTS")
    @GraphProperty(value = "comments", emptyPlaceholder = "null")
    public String comments;
}
