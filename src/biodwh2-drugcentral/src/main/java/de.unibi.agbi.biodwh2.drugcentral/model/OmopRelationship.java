package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "conceptId", "relationshipName", "conceptName",
"umlsCui", "snomedFullName", "cuiSemanticType", "snomedConceptId"})

public final class OmopRelationship {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("conceptId")
    public String conceptId;
    @JsonProperty("relationshipName")
    public String relationshipName;
    @JsonProperty("conceptName")
    public String conceptName;
    @JsonProperty("umlsCui")
    public String umlsCui;
    @JsonProperty("snomedFullName")
    public String snomedFullName;
    @JsonProperty("cuiSemanticType")
    public String cuiSemanticType;
    @JsonProperty("snomedConceptId")
    public String snomedConceptId;
}
