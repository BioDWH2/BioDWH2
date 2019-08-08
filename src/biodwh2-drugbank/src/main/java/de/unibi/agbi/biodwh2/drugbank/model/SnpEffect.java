package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SnpEffect {
    @JsonProperty("protein-name")
    public String proteinName;
    @JsonProperty("gene-symbol")
    public String geneSymbol;
    @JsonProperty("uniprot-id")
    public String uniprotId;
    @JsonProperty("rs-id")
    public String rsId;
    public String allele;
    @JsonProperty("defining-change")
    public String definingChange;
    public String description;
    @JsonProperty("pubmed-id")
    public String pubmedId;
}
