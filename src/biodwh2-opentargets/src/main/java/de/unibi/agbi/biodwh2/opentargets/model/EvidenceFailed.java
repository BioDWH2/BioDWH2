package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EvidenceFailed extends Evidence {
    @JsonProperty("resolvedTarget")
    public Boolean resolvedTarget;
    @JsonProperty("resolvedDisease")
    public Boolean resolvedDisease;
    @JsonProperty("excludedBiotype")
    public Boolean excludedBiotype;
    @JsonProperty("nullifiedScore")
    public Boolean nullifiedScore;
    @JsonProperty("markedDuplicate")
    public Boolean markedDuplicate;
}
