package de.unibi.agbi.biodwh2.rnadisease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"RNA_symbol", "RNA_type", "disease_name", "algorithm_score", "method_name", "RDID", "RD_score"})
public class PredictedEntry {
    @JsonProperty("RNA_symbol")
    public String rnaSymbol;
    @JsonProperty("RNA_type")
    public String rnaType;
    @JsonProperty("disease_name")
    public String diseaseName;
    @JsonProperty("algorithm_score")
    public String algorithmScore;
    @JsonProperty("method_name")
    public String methodName;
    @JsonProperty("RDID")
    public String rdId;
    @JsonProperty("RD_score")
    public String rdScore;
}
