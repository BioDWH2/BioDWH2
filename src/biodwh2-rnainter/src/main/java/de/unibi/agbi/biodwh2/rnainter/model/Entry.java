package de.unibi.agbi.biodwh2.rnainter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "RNAInterID", "Interactor1.Symbol", "Category1", "Species1", "Interactor2.Symbol", "Category2", "Species2",
        "Raw_ID1", "Raw_ID2", "score", "strong", "weak", "predict"
})
public class Entry {
    @JsonProperty("RNAInterID")
    public String rnaInterId;
    @JsonProperty("Interactor1.Symbol")
    public String interactor1Symbol;
    @JsonProperty("Category1")
    public String category1;
    @JsonProperty("Species1")
    public String species1;
    @JsonProperty("Interactor2.Symbol")
    public String interactor2Symbol;
    @JsonProperty("Category2")
    public String category2;
    @JsonProperty("Species2")
    public String species2;
    @JsonProperty("Raw_ID1")
    public String rawId1;
    @JsonProperty("Raw_ID2")
    public String rawId2;
    @JsonProperty("score")
    public String score;
    @JsonProperty("strong")
    public String strong;
    @JsonProperty("weak")
    public String weak;
    @JsonProperty("predict")
    public String predict;
}
