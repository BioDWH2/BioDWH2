package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "gene_name", "gene_claim_name", "entrez_id", "interaction_claim_source", "interaction_types", "drug_claim_name",
        "drug_claim_primary_name", "drug_name", "drug_chembl_id", "PMIDs"
})
public class Interaction {
    @JsonProperty("gene_name")
    public String geneName;
    @JsonProperty("gene_claim_name")
    public String geneClaimName;
    @JsonProperty("entrez_id")
    public String entrezId;
    @JsonProperty("interaction_claim_source")
    public String interactionClaimSource;
    @JsonProperty("interaction_types")
    public String interactionTypes;
    @JsonProperty("drug_claim_name")
    public String drugClaimName;
    @JsonProperty("drug_claim_primary_name")
    public String drugClaimPrimaryName;
    @JsonProperty("drug_name")
    public String drugName;
    @JsonProperty("drug_chembl_id")
    public String drugChemblId;
    @JsonProperty("PMIDs")
    public String pmids;
}
