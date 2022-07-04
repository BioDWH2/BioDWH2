package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"ontology_id", "name", "short_name"})
@GraphNodeLabel("Ontology")
public class Ontology {
    @JsonProperty("ontology_id")
    @GraphProperty("id")
    public Long ontologyId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("short_name")
    @GraphProperty("short_name")
    public String shortName;
}
