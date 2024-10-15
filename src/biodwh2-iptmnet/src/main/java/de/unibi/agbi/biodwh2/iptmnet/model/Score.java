package de.unibi.agbi.biodwh2.iptmnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"substrate_UniProtAC", "site", "enzyme_UniProtAC", "ptm_type", "score"})
public class Score {
    @JsonProperty("substrate_UniProtAC")
    public String substrateUniProtAccession;
    @JsonProperty("site")
    public String site;
    @JsonProperty("enzyme_UniProtAC")
    public String enzymeUniProtAccession;
    @JsonProperty("ptm_type")
    public String ptmType;
    @JsonProperty("score")
    public Integer score;
}
