package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "mirna_id", "auto_mirna", "exp_count", "5p_count", "5p_raw_count", "3p_count", "3p_raw_count", "5p_consistent",
        "5p_mature_consistent", "3p_consistent", "3p_mature_consistent", "5p_overhang", "3p_overhang",
        "energy_precursor", "energy_by_length", "paired_hairpin", "mirdeep_score"
})
@GraphNodeLabel("Confidence")
public class Confidence {
    @JsonProperty("mirna_id")
    public String mirnaId;
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("exp_count")
    @GraphProperty("exp_count")
    public Integer expCount;
    @JsonProperty("5p_count")
    @GraphProperty("5p_count")
    public String _5pCount;
    @JsonProperty("5p_raw_count")
    @GraphProperty("5p_raw_count")
    public String _5pRawCount;
    @JsonProperty("3p_count")
    @GraphProperty("3p_count")
    public String _3pCount;
    @JsonProperty("3p_raw_count")
    @GraphProperty("3p_raw_count")
    public String _3pRawCount;
    @JsonProperty("5p_consistent")
    @GraphProperty("5p_consistent")
    public String _5pConsistent;
    @JsonProperty("5p_mature_consistent")
    @GraphProperty("5p_mature_consistent")
    public String _5pMatureConsistent;
    @JsonProperty("3p_consistent")
    @GraphProperty("3p_consistent")
    public String _3pConsistent;
    @JsonProperty("3p_mature_consistent")
    @GraphProperty("3p_mature_consistent")
    public String _3pMatureConsistent;
    @JsonProperty("5p_overhang")
    @GraphProperty("5p_overhang")
    public String _5pOverhang;
    @JsonProperty("3p_overhang")
    @GraphProperty("3p_overhang")
    public String _3pOverhang;
    @JsonProperty("energy_precursor")
    @GraphProperty("energy_precursor")
    public String energyPrecursor;
    @JsonProperty("energy_by_length")
    @GraphProperty("energy_by_length")
    public String energyByLength;
    @JsonProperty("paired_hairpin")
    @GraphProperty("paired_hairpin")
    public String pairedHairpin;
    @JsonProperty("mirdeep_score")
    @GraphProperty("mirdeep_score")
    public String mirdeepScore;
}
