package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#ncbi_taxid", "cluster_id", "protein_id"})
public class ClusterProtein {
    @JsonProperty("#ncbi_taxid")
    public Integer ncbiTaxId;
    @JsonProperty("cluster_id")
    public String clusterId;
    @JsonProperty("protein_id")
    public String proteinId;
}
