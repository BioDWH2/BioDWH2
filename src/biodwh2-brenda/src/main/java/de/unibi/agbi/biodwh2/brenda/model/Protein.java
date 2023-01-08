package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Protein {
    @JsonProperty("accessions")
    public String[] accessions;
    @JsonProperty("source")
    public String source; // "enum": ["uniprot", "genbank"]
    @JsonProperty("comment")
    public String comment;
}
