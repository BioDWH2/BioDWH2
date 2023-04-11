package de.unibi.agbi.biodwh2.hpo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"hpo_id", "hpo_name", "ncbi_gene_id", "gene_symbol"})
public final class PhenotypeToGenesEntry {
    @JsonProperty("hpo_id")
    public String hpoId;
    @JsonProperty("hpo_name")
    public String hpoName;
    @JsonProperty("ncbi_gene_id")
    public Integer ncbiGeneId;
    @JsonProperty("gene_symbol")
    public String geneSymbol;
}
