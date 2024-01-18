package de.unibi.agbi.biodwh2.biom2metdisease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "Category", "Species", "Biomolecule name", "Biomolecule ID", "Disease name", "Disease ontology",
        "ICD-10 classification", "Tissue", "PubMed_ID", "Interaction gene symbol", "Expression direction",
        "Experimental method", "Experimental method classification", "Throughput", "Description", "Reference title",
        "Year"
})
public class Associations {
    @JsonProperty("Category")
    public String category;
    @JsonProperty("Species")
    public String species;
    @JsonProperty("Biomolecule name")
    public String biomoleculeName;
    @JsonProperty("Biomolecule ID")
    public String biomoleculeID;
    @JsonProperty("Disease name")
    public String diseaseName;
    @JsonProperty("Disease ontology")
    public String diseaseOntology;
    @JsonProperty("ICD-10 classification")
    public String icd10Classification;
    @JsonProperty("Tissue")
    public String tissue;
    @JsonProperty("PubMed_ID")
    public String pubmedID;
    @JsonProperty("Interaction gene symbol")
    public String interactionGeneSymbol;
    @JsonProperty("Expression direction")
    public String expressionDirection;
    @JsonProperty("Experimental method")
    public String experimentalMethod;
    @JsonProperty("Experimental method classification")
    public String experimentalMethodClassification;
    @JsonProperty("Throughput")
    public String throughput;
    @JsonProperty("Description")
    public String description;
    @JsonProperty("Reference title")
    public String referenceTitle;
    @JsonProperty("Year")
    public String year;
}
