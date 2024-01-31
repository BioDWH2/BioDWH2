package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "ID", "STATUS", "CHEBI_ACCESSION", "SOURCE", "PARENT_ID", "NAME", "DEFINITION", "MODIFIED_ON", "CREATED_BY",
        "STAR"
})
@GraphNodeLabel(ChEBIGraphExporter.COMPOUND_LABEL)
public class Compound {
    @JsonProperty("ID")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("STATUS")
    @GraphProperty("status")
    public String status;
    @JsonProperty("CHEBI_ACCESSION")
    @GraphProperty("accession")
    public String chebiAccession;
    @JsonProperty("SOURCE")
    @GraphProperty("source")
    public String source;
    @JsonProperty("PARENT_ID")
    public String parentId;
    @JsonProperty("NAME")
    @GraphProperty(value = "name", emptyPlaceholder = "null")
    public String name;
    @JsonProperty("DEFINITION")
    @GraphProperty(value = "definition", emptyPlaceholder = "null")
    public String definition;
    @JsonProperty("MODIFIED_ON")
    @GraphProperty(value = "modified_on", emptyPlaceholder = "null")
    public String modifiedOn;
    @JsonProperty("CREATED_BY")
    @GraphProperty(value = "created_by", emptyPlaceholder = "null")
    public String createdBy;
    @JsonProperty("STAR")
    @GraphProperty("star")
    public Integer star;
}
