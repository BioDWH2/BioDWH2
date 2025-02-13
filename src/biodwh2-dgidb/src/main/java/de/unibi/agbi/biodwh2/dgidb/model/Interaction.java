package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbGraphExporter;

@JsonPropertyOrder({
        "gene_claim_name", "gene_concept_id", "gene_name", "drug_claim_name", "drug_concept_id", "drug_name",
        "drug_is_approved", "drug_is_immunotherapy", "drug_is_antineoplastic", "interaction_source_db_name",
        "interaction_source_db_version", "interaction_types", "interaction_score", "drug_specificity_score",
        "gene_specificity_score", "evidence_score"
})
@GraphEdgeLabel(DGIdbGraphExporter.INTERACTS_WITH_LABEL)
public class Interaction {
    @JsonProperty("gene_claim_name")
    public String geneClaimName;
    @JsonProperty("gene_concept_id")
    public String geneConceptId;
    @JsonProperty("gene_name")
    public String geneName;
    @JsonProperty("drug_claim_name")
    public String drugClaimName;
    @JsonProperty("drug_concept_id")
    public String drugConceptId;
    @JsonProperty("drug_name")
    public String drugName;
    @JsonProperty("interaction_source_db_name")
    @GraphProperty("source_db")
    public String interactionSourceDBName;
    @JsonProperty("interaction_source_db_version")
    @GraphProperty("source_db_version")
    public String interactionSourceDBVersion;
    @JsonProperty("interaction_types")
    @GraphProperty(value = "type")
    public String interactionType;
    @JsonProperty("interaction_score")
    @GraphProperty(value = "score")
    public String interactionScore;
    @JsonProperty("drug_is_approved")
    public String drugIsApproved;
    @JsonProperty("drug_is_immunotherapy")
    public String drugIsImmunotherapy;
    @JsonProperty("drug_is_antineoplastic")
    public String drugIsAntiNeoplastic;
    @JsonProperty("drug_specificity_score")
    @GraphProperty(value = "drug_specificity_score")
    public String drugSpecificityScore;
    @JsonProperty("gene_specificity_score")
    @GraphProperty(value = "gene_specificity_score")
    public String geneSpecificityScore;
    @JsonProperty("evidence_score")
    @GraphProperty(value = "evidence_score")
    public String evidenceScore;
}
