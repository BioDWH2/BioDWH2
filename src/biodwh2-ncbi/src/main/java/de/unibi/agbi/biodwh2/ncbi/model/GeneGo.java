package de.unibi.agbi.biodwh2.ncbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#tax_id", "GeneID", "GO_ID", "Evidence", "Qualifier", "GO_term", "PubMed", "Category"})
public class GeneGo {
    @JsonProperty("#tax_id")
    public String taxonomyId;
    @JsonProperty("GeneID")
    public String geneId;
    @JsonProperty("GO_ID")
    public String goId;
    @JsonProperty("Evidence")
    public String evidence;
    @JsonProperty("Qualifier")
    public String qualifier;
    @JsonProperty("GO_term")
    public String goTerm;
    @JsonProperty("PubMed")
    public String pubMedIds;
    @JsonProperty("Category")
    public String category;
}
