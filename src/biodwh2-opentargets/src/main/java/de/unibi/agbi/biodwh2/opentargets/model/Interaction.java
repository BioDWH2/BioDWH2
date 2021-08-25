package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Interaction {
    @JsonProperty("sourceDatabase")
    public String sourceDatabase;
    @JsonProperty("targetA")
    public String targetA;
    @JsonProperty("intA")
    public String intA;
    @JsonProperty("intABiologicalRole")
    public String intABiologicalRole;
    @JsonProperty("targetB")
    public String targetB;
    @JsonProperty("intB")
    public String intB;
    @JsonProperty("intBBiologicalRole")
    public String intBBiologicalRole;
    @JsonProperty("count")
    public Integer count;
    @JsonProperty("scoring")
    public Double scoring;
    @JsonProperty("speciesA")
    public Species speciesA;
    @JsonProperty("speciesB")
    public Species speciesB;

    public static class Species {
        @JsonProperty("mnemonic")
        public String mnemonic;
        @JsonProperty("scientific_name")
        public String scientificName;
        @JsonProperty("taxon_id")
        public Integer taxonId;
    }
}
