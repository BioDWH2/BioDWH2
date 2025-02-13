package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"gene_claim_name", "gene_category_name", "source_db_name", "source_db_version"})
public class Category {
    @JsonProperty("gene_claim_name")
    public String name;
    @JsonProperty("gene_category_name")
    public String category;
    @JsonProperty("source_db_name")
    public String sourceDBName;
    @JsonProperty("source_db_version")
    public String sourceDBVersion;
}
