package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbGraphExporter;

@JsonPropertyOrder({
        "gene_claim_name", "gene_concept_id", "gene_name", "interaction_source_db_name",
        "interaction_source_db_version", "interaction_type", "interaction_score", "drug_claim_name", "drug_concept_id",
        "drug_name", "approved", "immunotherapy", "anti_neoplastic"
})
@GraphEdgeLabel(DGIdbGraphExporter.INTERACTS_WITH_LABEL)
public class Interaction {
    @JsonProperty("gene_claim_name")
    public String gene_claim_name;
    @JsonProperty("gene_concept_id")
    public String gene_concept_id;
    @JsonProperty("gene_name")
    public String gene_name;
    @JsonProperty("interaction_source_db_name")
    @GraphProperty("source_db")
    public String interaction_source_db_name;
    @JsonProperty("interaction_source_db_version")
    @GraphProperty("source_db_version")
    public String interaction_source_db_version;
    @JsonProperty("interaction_type")
    @GraphProperty(value = "type", emptyPlaceholder = "NULL")
    public String interaction_type;
    @JsonProperty("interaction_score")
    @GraphProperty(value = "score", emptyPlaceholder = "NULL")
    public String interaction_score;
    @JsonProperty("drug_claim_name")
    public String drug_claim_name;
    @JsonProperty("drug_concept_id")
    public String drug_concept_id;
    @JsonProperty("drug_name")
    public String drug_name;
    @JsonProperty("approved")
    public String approved;
    @JsonProperty("immunotherapy")
    public String immunotherapy;
    @JsonProperty("anti_neoplastic")
    public String anti_neoplastic;
}
