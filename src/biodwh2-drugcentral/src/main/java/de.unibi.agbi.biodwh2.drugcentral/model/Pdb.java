package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "struct_id", "pdb", "chain_id", "accession", "title", "pubmed_id", "exp_method", "deposition_date",
        "ligand_id"
})
public final class Pdb {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("pdb")
    public String pdb;
    @JsonProperty("chain_id")
    public String chainId;
    @JsonProperty("accession")
    public String accession;
    @JsonProperty("title")
    public String title;
    @JsonProperty("pubmed_id")
    public String pubmedId;
    @JsonProperty("exp_method")
    public String expMethod;
    @JsonProperty("deposition_date")
    public String depositionDate;
    @JsonProperty("ligand_id")
    public String ligandId;
}
