package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("SNPEffect")
public final class SnpEffect {
    @JsonProperty("protein-name")
    @GraphProperty("protein_name")
    public String proteinName;
    @JsonProperty("gene-symbol")
    @GraphProperty("gene_symbol")
    public String geneSymbol;
    @JsonProperty("uniprot-id")
    @GraphProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("rs-id")
    @GraphProperty("rs_id")
    public String rsId;
    @GraphProperty("allele")
    public String allele;
    @JsonProperty("defining-change")
    @GraphProperty("defining_change")
    public String definingChange;
    @GraphProperty("description")
    public String description;
    @JsonProperty("pubmed-id")
    @GraphProperty("pubmed_id")
    public Integer pubmedId;
}
