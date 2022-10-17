package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "auto_id", "organism", "division", "name", "taxon_id", "taxonomy", "genome_assembly", "genome_accession",
        "ensembl_db"
})
public class MirnaSpecies {
    @JsonProperty("auto_id")
    public Long autoId;
    @JsonProperty("organism")
    public String organism;
    @JsonProperty("division")
    public String division;
    @JsonProperty("name")
    public String name;
    @JsonProperty("taxon_id")
    public Long taxonId;
    @JsonProperty("taxonomy")
    public String taxonomy;
    @JsonProperty("genome_assembly")
    public String genomeAssembly;
    @JsonProperty("genome_accession")
    public String genomeAccession;
    @JsonProperty("ensembl_db")
    public String ensemblDb;
}
