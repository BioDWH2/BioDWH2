package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssociationByDatatype {
    @JsonProperty("diseaseId")
    public String diseaseId;
    @JsonProperty("targetId")
    public String targetId;
    @JsonProperty("datatypeId")
    public String datatypeId;
    @JsonProperty("score")
    public Double score;
    @JsonProperty("evidenceCount")
    public Integer evidenceCount;
}
