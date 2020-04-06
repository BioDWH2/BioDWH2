package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {
        "id", "struct_id", "pdb", "chain_id", "accession", "title", "pubmed_id", "exp_method", "deposition_date",
        "ligand_id"
})
@NodeLabels({"PDB"})
public final class Pdb {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("struct_id")
    @GraphProperty("struct_id")
    public String structId;
    @JsonProperty("pdb")
    @GraphProperty("pdb")
    public String pdb;
    @JsonProperty("chain_id")
    @GraphProperty("chain_id")
    public String chainId;
    @JsonProperty("accession")
    @GraphProperty("accession")
    public String accession;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
    @JsonProperty("pubmed_id")
    @GraphProperty("pubmed_id")
    public String pubmedId;
    @JsonProperty("exp_method")
    @GraphProperty("exp_method")
    public String expMethod;
    @JsonProperty("deposition_date")
    @GraphProperty("deposition_date")
    public String depositionDate;
    @JsonProperty("ligand_id")
    @GraphProperty("ligand_id")
    public String ligandId;
}
