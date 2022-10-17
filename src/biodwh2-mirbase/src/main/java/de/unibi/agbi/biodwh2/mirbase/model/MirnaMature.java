package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "auto_mature", "mature_name", "previous_mature_id", "mature_acc", "evidence", "experiment", "similarity",
        "dead_flag"
})
public class MirnaMature {
    @JsonProperty("auto_mature")
    public Long autoMature;
    @JsonProperty("mature_name")
    public String matureName;
    @JsonProperty("previous_mature_id")
    public String previousMatureId;
    @JsonProperty("mature_acc")
    public String matureAcc;
    @JsonProperty("evidence")
    public String evidence;
    @JsonProperty("experiment")
    public String experiment;
    @JsonProperty("similarity")
    public String similarity;
    @JsonProperty("dead_flag")
    public Integer deadFlag;
}
