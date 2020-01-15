package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "accession", "swissprot", "organism"})

public final class TargetComponent {
    @JsonProperty("id")
    public String id;
    @JsonProperty("accession")
    public String accession;
    @JsonProperty("swissprot")
    public String swissprot;
    @JsonProperty("organism")
    public String organism;
    @JsonProperty("name")
    public String name;
    @JsonProperty("gene")
    public String gene;
    @JsonProperty("geneId")
    public String geneId;
    @JsonProperty("tdl")
    public String tdl;
}
