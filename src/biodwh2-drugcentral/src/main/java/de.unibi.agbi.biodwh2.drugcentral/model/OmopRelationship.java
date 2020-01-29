package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "struct_id", "concept_id", "relationship_name", "concept_name", "umls_cui", "snomed_full_name",
        "cui_semantic_type", "snomed_concept_id"
})

public final class OmopRelationship {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("concept_id")
    public String conceptId;
    @JsonProperty("relationship_name")
    public String relationshipName;
    @JsonProperty("concept_name")
    public String conceptName;
    @JsonProperty("umls_cui")
    public String umlsCui;
    @JsonProperty("snomed_full_name")
    public String snomedFullName;
    @JsonProperty("cui_semantic_type")
    public String cuiSemanticType;
    @JsonProperty("snomed_concept_id")
    public String snomedConceptId;
}
