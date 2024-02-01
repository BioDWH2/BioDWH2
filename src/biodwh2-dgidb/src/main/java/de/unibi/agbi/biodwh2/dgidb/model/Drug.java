package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbGraphExporter;

@JsonPropertyOrder({
        "drug_claim_name", "nomenclature", "concept_id", "drug_name", "approved", "immunotherapy", "anti_neoplastic",
        "source_db_name", "source_db_version"
})
@GraphNodeLabel(DGIdbGraphExporter.DRUG_LABEL)
public class Drug {
    @JsonProperty("drug_claim_name")
    @GraphProperty("claim_name")
    public String drugClaimName;
    @JsonProperty("nomenclature")
    @GraphProperty("nomenclature")
    public String nomenclature;
    @JsonProperty("concept_id")
    @GraphProperty("concept_id")
    public String conceptId;
    @JsonProperty("drug_name")
    @GraphProperty("name")
    public String drugName;
    @JsonProperty("approved")
    @GraphBooleanProperty(value = "approved", truthValue = "TRUE")
    public String approved;
    @JsonProperty("immunotherapy")
    @GraphBooleanProperty(value = "immunotherapy", truthValue = "TRUE")
    public String immunotherapy;
    @JsonProperty("anti_neoplastic")
    @GraphBooleanProperty(value = "anti_neoplastic", truthValue = "TRUE")
    public String antiNeoplastic;
    @JsonProperty("source_db_name")
    @GraphProperty("source_db_name")
    public String sourceDBName;
    @JsonProperty("source_db_version")
    @GraphProperty("source_db_version")
    public String sourceDBVersion;
}
