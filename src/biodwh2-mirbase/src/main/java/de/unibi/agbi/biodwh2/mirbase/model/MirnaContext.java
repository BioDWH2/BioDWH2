package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "auto_mirna", "transcript_id", "overlap_sense", "overlap_type", "number", "transcript_source", "transcript_name"
})
@GraphNodeLabel("Context")
public class MirnaContext {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("transcript_id")
    @GraphProperty("transcript_id")
    public String transcriptId;
    @JsonProperty("overlap_sense")
    @GraphProperty("overlap_sense")
    public String overlapSense;
    @JsonProperty("overlap_type")
    @GraphProperty("overlap_type")
    public String overlapType;
    @JsonProperty("number")
    @GraphProperty("number")
    public Integer number;
    @JsonProperty("transcript_source")
    @GraphProperty("transcript_source")
    public String transcriptSource;
    @JsonProperty("transcript_name")
    @GraphProperty("transcript_name")
    public String transcriptName;
}
