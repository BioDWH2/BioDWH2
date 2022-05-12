package de.unibi.agbi.biodwh2.gene2phenotype.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "gene symbol", "gene mim", "disease name", "disease mim", "confidence category", "allelic requirement",
        "mutation consequence", "phenotypes", "organ specificity list", "pmids", "panel", "prev symbols", "hgnc id",
        "gene disease pair entry date", "cross cutting modifier", "mutation consequence flag", "confidence value flag",
        "comments", "variant consequence", "disease ontology"
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
    @JsonProperty("confidence category")
    public String confidenceCategory;
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
    public String panel;
    @JsonProperty("prev symbols")
    public String prevSymbols;
    @JsonProperty("hgnc id")
    public Integer hgncId;
    @JsonProperty("gene disease pair entry date")
    public String entryDate;
    @JsonProperty("cross cutting modifier")
    public String crossCuttingModifier;
    @JsonProperty("mutation consequence flag")
    public String mutationConsequenceFlag;
    @JsonProperty("confidence value flag")
    public String confidenceValueFlag;
    @JsonProperty("comments")
    public String comments;
    @JsonProperty("variant consequence")
    public String variantConsequence;
    @JsonProperty("disease ontology")
    public String diseaseOntology;
}
