package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"SnpAdverseDrugReaction"})
public final class SnpAdverseDrugReaction {
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
    @JsonProperty("adverse-reaction")
    @GraphProperty("adverse_reaction")
    public String adverseReaction;
    @GraphProperty("description")
    public String description;
    @JsonProperty("pubmed-id")
    @GraphProperty("pubmed_id")
    public String pubmedId;
}
