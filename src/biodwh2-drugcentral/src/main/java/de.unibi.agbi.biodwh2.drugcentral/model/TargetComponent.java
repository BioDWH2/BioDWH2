package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "accession", "swissprot", "organism", "name", "gene", "geneid", "tdl"})
@NodeLabels("TargetComponent")
public final class TargetComponent {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("accession")
    @GraphProperty("accession")
    public String accession;
    @JsonProperty("swissprot")
    @GraphProperty("swissprot")
    public String swissProt;
    @JsonProperty("organism")
    @GraphProperty("organism")
    public String organism;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("gene")
    @GraphProperty("gene")
    public String gene;
    @JsonProperty("geneid")
    @GraphProperty("geneid")
    public String geneId;
    @JsonProperty("tdl")
    @GraphProperty("tdl")
    public String tdl;
}
