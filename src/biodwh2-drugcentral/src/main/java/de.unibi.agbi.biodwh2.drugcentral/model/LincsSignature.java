package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId1", "structId2", "isParent1", "isParent2", "cellId", "rmsd", "rmsdNorm",
"pearson", "euclid"})

public final class LincsSignature {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId1")
    public String structId1;
    @JsonProperty("structId2")
    public String structId2;
    @JsonProperty("isParent1")
    public String isParent1;
    @JsonProperty("isParent2")
    public String isParent2;
    @JsonProperty("cellId")
    public String cellId;
    @JsonProperty("rmsd")
    public String rmsd;
    @JsonProperty("rmsdNorm")
    public String rmsdNorm;
    @JsonProperty("pearson")
    public String pearson;
    @JsonProperty("euclid")
    public String euclid;
}
