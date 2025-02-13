package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProteinDataset {
    @SuppressWarnings("unused")
    @JsonProperty("id")
    public String id;
    @JsonProperty("organism")
    public String organism;
    @JsonProperty("accessions")
    public String[] accessions;
    @JsonProperty("references")
    public String[] references;
    /**
     * "enum": ["uniprot", "genbank", "swissprot", "trembl"]
     */
    @JsonProperty("source")
    public String source;
    @JsonProperty("comment")
    public String comment;
}
