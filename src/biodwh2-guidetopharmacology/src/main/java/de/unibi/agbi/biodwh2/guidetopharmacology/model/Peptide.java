package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "ligand_id", "one_letter_seq", "three_letter_seq", "post_translational_modifications", "chemical_modifications",
        "medical_relevance", "helm_notation"
})
public class Peptide {
    @JsonProperty("ligand_id")
    public Long ligandId;
    @JsonProperty("one_letter_seq")
    @GraphProperty("one_letter_seq")
    public String oneLetterSeq;
    @JsonProperty("three_letter_seq")
    @GraphProperty("three_letter_seq")
    public String threeLetterSeq;
    @JsonProperty("post_translational_modifications")
    @GraphProperty("post_translational_modifications")
    public String postTranslationalModifications;
    @JsonProperty("chemical_modifications")
    @GraphProperty("chemical_modifications")
    public String chemicalModifications;
    @JsonProperty("medical_relevance")
    @GraphProperty("medical_relevance")
    public String medicalRelevance;
    @JsonProperty("helm_notation")
    @GraphProperty("helm_notation")
    public String helmNotation;
}
