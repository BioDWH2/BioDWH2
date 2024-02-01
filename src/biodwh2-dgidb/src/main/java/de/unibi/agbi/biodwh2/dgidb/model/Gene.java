package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "nomenclature", "concept_id", "gene_claim_name", "source_db_name", "source_db_version"})
public class Gene {
    @JsonProperty("name")
    public String name;
    @JsonProperty("nomenclature")
    public String nomenclature;
    @JsonProperty("concept_id")
    public String conceptId;
    @JsonProperty("gene_claim_name")
    public String geneClaimName;
    @JsonProperty("source_db_name")
    public String sourceDBName;
    @JsonProperty("source_db_version")
    public String sourceDBVersion;
}
