package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Drug {
    public static class DrugClaim extends Claim {
        public String name;
        @JsonProperty(value = "primary_name")
        public String primaryName;
        public List<String> aliases;
    }

    public String name;
    @JsonProperty(value = "chembl_id")
    public String chemblId;
    @JsonProperty(value = "fda_approved")
    public boolean fdaApproved;
    public boolean immunotherapy;
    @JsonProperty(value = "anti_neoplastic")
    public boolean antiNeoplastic;
    public List<String> alias;
    public List<Integer> pmids;
    public List<Attribute> attributes;
    @JsonProperty(value = "drug_claims")
    public List<DrugClaim> drugClaims;
}
