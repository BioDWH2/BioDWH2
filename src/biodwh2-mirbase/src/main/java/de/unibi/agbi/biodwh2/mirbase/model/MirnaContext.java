package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "auto_mirna", "transcript_id", "overlap_sense", "overlap_type", "number", "transcript_source", "transcript_name"
})
public class MirnaContext {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("transcript_id")
    public String transcriptId;
    @JsonProperty("overlap_sense")
    public String overlapSense;
    @JsonProperty("overlap_type")
    public String overlapType;
    @JsonProperty("number")
    public Integer number;
    @JsonProperty("transcript_source")
    public String transcriptSource;
    @JsonProperty("transcript_name")
    public String transcriptName;
}
