package de.unibi.agbi.biodwh2.ptmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "uniprot_id", "gene_name", "disease", "ptmType", "state", "residue", "position", "literature", "species"
})
public class PTM {
    @JsonProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("gene_name")
    public String geneName;
    @JsonProperty("disease")
    public String disease;
    @JsonProperty("ptmType")
    public String ptmType;
    @JsonProperty("state")
    public String state;
    @JsonProperty("residue")
    public String residue;
    @JsonProperty("position")
    public String position;
    @JsonProperty("literature")
    public String literature;
    @JsonProperty("species")
    public String species;
}
