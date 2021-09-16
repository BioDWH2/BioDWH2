package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssociationByOverall {
    @JsonProperty("diseaseId")
    public String diseaseId;
    @JsonProperty("targetId")
    public String targetId;
    @JsonProperty("score")
    public Double score;
    @JsonProperty("evidenceCount")
    public Integer evidenceCount;
}
