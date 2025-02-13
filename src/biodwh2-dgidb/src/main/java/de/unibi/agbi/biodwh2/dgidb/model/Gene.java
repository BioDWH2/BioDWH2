package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "gene_claim_name", "nomenclature", "concept_id", "gene_name", "source_db_name", "source_db_version"
})
public class Gene {
    @JsonProperty("gene_claim_name")
    public String geneClaimName;
    @JsonProperty("nomenclature")
    public String nomenclature;
    @JsonProperty("concept_id")
    public String conceptId;
    @JsonProperty("gene_name")
    public String name;
    @JsonProperty("source_db_name")
    public String sourceDBName;
    @JsonProperty("source_db_version")
    public String sourceDBVersion;
}
