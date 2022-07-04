package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"ontology_id", "term_id", "term", "description"})
@GraphNodeLabel("OntologyTerm")
public class OntologyTerm {
    @JsonProperty("ontology_id")
    public Long ontologyId;
    @JsonProperty("term_id")
    @GraphProperty("id")
    public String termId;
    @JsonProperty("term")
    @GraphProperty("term")
    public String term;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
}
