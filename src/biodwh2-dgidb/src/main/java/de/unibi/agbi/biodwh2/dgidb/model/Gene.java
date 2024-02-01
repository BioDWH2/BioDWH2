package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbGraphExporter;

@JsonPropertyOrder({"name", "nomenclature", "concept_id", "gene_claim_name", "source_db_name", "source_db_version"})
@GraphNodeLabel(DGIdbGraphExporter.GENE_LABEL)
public class Gene {
    @GraphProperty("name")
    @JsonProperty("name")
    public String name;
    @GraphProperty("nomenclature")
    @JsonProperty("nomenclature")
    public String nomenclature;
    @GraphProperty("concept_id")
    @JsonProperty("concept_id")
    public String conceptId;
    @GraphProperty("gene_claim_name")
    @JsonProperty("gene_claim_name")
    public String geneClaimName;
    @GraphProperty("source_db_name")
    @JsonProperty("source_db_name")
    public String sourceDBName;
    @GraphProperty("source_db_version")
    @JsonProperty("source_db_version")
    public String sourceDBVersion;
}
