package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"entrez_gene_symbol", "gene_long_name", "category_sources", "category"})
public class Category {
    @JsonProperty("entrez_gene_symbol")
    public String entrezGeneSymbol;
    @JsonProperty("gene_long_name")
    public String geneLongName;
    @JsonProperty("category_sources")
    public String categorySources;
    @JsonProperty("category")
    public String category;
}
