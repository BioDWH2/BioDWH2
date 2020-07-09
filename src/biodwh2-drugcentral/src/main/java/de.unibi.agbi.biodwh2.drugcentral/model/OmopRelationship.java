package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "struct_id", "concept_id", "relationship_name", "concept_name", "umls_cui", "snomed_full_name",
        "cui_semantic_type", "snomed_conceptid"
})
@NodeLabel("OmopRelationship")
public final class OmopRelationship {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("concept_id")
    @GraphProperty("concept_id")
    public String conceptId;
    @JsonProperty("relationship_name")
    @GraphProperty("relationship_name")
    public String relationshipName;
    @JsonProperty("concept_name")
    @GraphProperty("concept_name")
    public String conceptName;
    @JsonProperty("umls_cui")
    @GraphProperty("umls_cui")
    public String umlsCui;
    @JsonProperty("snomed_full_name")
    @GraphProperty("snomed_full_name")
    public String snomedFullName;
    @JsonProperty("cui_semantic_type")
    @GraphProperty("cui_semantic_type")
    public String cuiSemanticType;
    @JsonProperty("snomed_conceptid")
    @GraphProperty("snomed_concept_id")
    public String snomedConceptId;
}
