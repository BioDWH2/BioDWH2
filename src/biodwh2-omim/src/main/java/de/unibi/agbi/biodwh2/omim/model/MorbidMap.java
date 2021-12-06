package de.unibi.agbi.biodwh2.omim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Phenotype", "Gene Symbols", "MIM Number", "Cyto Location"})
public class MorbidMap {
    @JsonProperty("Phenotype")
    public String phenotype;
    @JsonProperty("Gene Symbols")
    public String geneSymbols;
    @JsonProperty("MIM Number")
    public String mimNumber;
    @JsonProperty("Cyto Location")
    public String cytoLocation;
}
