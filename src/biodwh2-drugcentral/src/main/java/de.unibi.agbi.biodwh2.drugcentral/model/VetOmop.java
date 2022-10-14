package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.drugcentral.etl.DrugCentralGraphExporter;

@JsonPropertyOrder({"omopid", "struct_id", "species", "relationship_type", "concept_name"})
@GraphNodeLabel(DrugCentralGraphExporter.VET_OMOP_LABEL)
public class VetOmop {
    @JsonProperty("omopid")
    public Long omopId;
    @JsonProperty("struct_id")
    public Long structId;
    @JsonProperty("species")
    public String species;
    @JsonProperty("relationship_type")
    public String relationshipType;
    @JsonProperty("concept_name")
    @GraphProperty("concept_name")
    public String conceptName;
}
