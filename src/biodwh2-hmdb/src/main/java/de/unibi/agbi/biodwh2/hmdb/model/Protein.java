package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Protein {
    @JsonProperty("protein_accession")
    public String proteinAccession;
    public String name;
    @JsonProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("gene_name")
    public String geneName;
    @JsonProperty("protein_type")
    public String proteinType;
}
