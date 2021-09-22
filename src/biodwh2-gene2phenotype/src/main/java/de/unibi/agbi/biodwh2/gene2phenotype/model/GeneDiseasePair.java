package de.unibi.agbi.biodwh2.gene2phenotype.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
        "gene symbol", "gene mim", "disease name", "disease mim", "DDD category", "allelic requirement",
        "mutation consequence", "phenotypes", "organ specificity list", "pmids", "panel", "prev symbols", "hgnc id",
        "gene disease pair entry date"
})
public class GeneDiseasePair {
    @JsonProperty("gene symbol")
    public String geneSymbol;
    @JsonProperty("gene mim")
    public String geneMim;
    @JsonProperty("disease name")
    public String diseaseName;
    @JsonProperty("disease mim")
    public String diseaseMim;
    @JsonProperty("DDD category")
    public DiseaseConfidence diseaseConfidence;
    @JsonProperty("allelic requirement")
    public String allelicRequirement;
    @JsonProperty("mutation consequence")
    public String mutationConsequence;
    @JsonProperty("phenotypes")
    public String phenotypes;
    @JsonProperty("organ specificity list")
    public String organSpecificityList;
    @JsonProperty("pmids")
    public String pmids;
    @JsonProperty("panel")
    public Panel panel;
    @JsonProperty("prev symbols")
    public String prevSymbols;
    @JsonProperty("hgnc id")
    public int hgncId;
    @JsonProperty("gene disease pair entry date")
    public String entryDate;
}
