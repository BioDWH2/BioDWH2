package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Gene {
    public static class GeneClaim extends Claim {
        public String name;
        public List<String> aliases;
    }

    public String name;
    @JsonProperty(value = "long_name")
    public String longName;
    @JsonProperty(value = "entrez_id")
    public int entrezId;
    public List<String> aliases;
    public List<Integer> pmids;
    public List<Attribute> attributes;
    public List<String> categories;
    @JsonProperty(value = "gene_claims")
    public List<GeneClaim> geneClaims;
}
