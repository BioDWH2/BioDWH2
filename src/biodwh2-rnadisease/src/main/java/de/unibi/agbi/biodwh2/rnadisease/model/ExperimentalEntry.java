package de.unibi.agbi.biodwh2.rnadisease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "RDID", "specise", "RNA Symbol", "RNA Type", "Disease Name", "DO ID", "MeSH ID", "KEGG disease ID", "PMID",
        "score"
})
public class ExperimentalEntry {
    @JsonProperty("RDID")
    public String rdId;
    @JsonProperty("specise")
    public String species;
    @JsonProperty("RNA Symbol")
    public String rnaSymbol;
    @JsonProperty("RNA Type")
    public String rnaType;
    @JsonProperty("Disease Name")
    public String diseaseName;
    @JsonProperty("DO ID")
    public String doId;
    @JsonProperty("MeSH ID")
    public String meshId;
    @JsonProperty("KEGG disease ID")
    public String keggDiseaseId;
    @JsonProperty("PMID")
    public Integer pmid;
    @JsonProperty("score")
    public String score;
}
