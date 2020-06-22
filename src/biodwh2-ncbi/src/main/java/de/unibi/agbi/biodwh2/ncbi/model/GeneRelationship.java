package de.unibi.agbi.biodwh2.ncbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#tax_id", "GeneID", "relationship", "Other_tax_id", "Other_GeneID"})
public class GeneRelationship {
    @JsonProperty("#tax_id")
    public String taxonomyId;
    @JsonProperty("GeneID")
    public String geneId;
    @JsonProperty("relationship")
    public String relationship;
    @JsonProperty("Other_tax_id")
    public String otherTaxonomyId;
    @JsonProperty("Other_GeneID")
    public String otherGeneId;
}
