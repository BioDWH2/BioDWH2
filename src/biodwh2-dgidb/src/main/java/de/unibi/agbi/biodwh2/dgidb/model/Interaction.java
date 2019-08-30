package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Interaction {
    public static class InteractionClaim extends Claim {
        public String drug;
        public String gene;
        @JsonProperty(value = "interaction_types")
        public List<String> interactionTypes;
    }

    public String id;
    @JsonProperty(value = "gene_name")
    public String geneName;
    @JsonProperty(value = "entrez_id")
    public int entrezId;
    @JsonProperty(value = "drug_name")
    public String drugName;
    @JsonProperty(value = "chembl_id")
    public String chemblId;
    public List<Integer> publications;
    @JsonProperty(value = "interaction_types")
    public List<String> interactionTypes;
    public List<String> sources;
    public List<Attribute> attributes;
    @JsonProperty(value = "interaction_claims")
    public List<InteractionClaim> interactionClaims;
}
