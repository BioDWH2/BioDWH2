package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "mirna_id", "auto_mirna", "exp_count", "5p_count", "5p_raw_count", "3p_count", "3p_raw_count", "5p_consistent",
        "5p_mature_consistent", "3p_consistent", "3p_mature_consistent", "5p_overhang", "3p_overhang",
        "energy_precursor", "energy_by_length", "paired_hairpin", "mirdeep_score"
})
public class Confidence {
    @JsonProperty("mirna_id")
    public String mirnaId;
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("exp_count")
    public Integer expCount;
    @JsonProperty("5p_count")
    public Integer _5pCount;
    @JsonProperty("5p_raw_count")
    public Integer _5pRawCount;
    @JsonProperty("3p_count")
    public Integer _3pCount;
    @JsonProperty("3p_raw_count")
    public Integer _3pRawCount;
    @JsonProperty("5p_consistent")
    public String _5pConsistent;
    @JsonProperty("5p_mature_consistent")
    public String _5pMatureConsistent;
    @JsonProperty("3p_consistent")
    public String _3pConsistent;
    @JsonProperty("3p_mature_consistent")
    public String _3pMatureConsistent;
    @JsonProperty("5p_overhang")
    public Integer _5pOverhang;
    @JsonProperty("3p_overhang")
    public Integer _3pOverhang;
    @JsonProperty("energy_precursor")
    public String energyPrecursor;
    @JsonProperty("energy_by_length")
    public String energyByLength;
    @JsonProperty("paired_hairpin")
    public String pairedHairpin;
    @JsonProperty("mirdeep_score")
    public String mirdeepScore;
}
