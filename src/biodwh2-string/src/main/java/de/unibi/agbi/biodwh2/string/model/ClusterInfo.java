package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#ncbi_taxid", "cluster_id", "cluster_size", "best_described_by"})
public class ClusterInfo {
    @JsonProperty("#ncbi_taxid")
    public Integer ncbiTaxId;
    @JsonProperty("cluster_id")
    public String clusterId;
    @JsonProperty("cluster_size")
    public Integer clusterSize;
    @JsonProperty("best_described_by")
    public String bestDescribedBy;
}
