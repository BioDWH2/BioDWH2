package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#ncbi_taxid", "child_cluster_id", "parent_cluster_id"})
public class ClusterTree {
    @JsonProperty("#ncbi_taxid")
    public Integer ncbiTaxId;
    @JsonProperty("child_cluster_id")
    public String childClusterId;
    @JsonProperty("parent_cluster_id")
    public String parentClusterId;
}
