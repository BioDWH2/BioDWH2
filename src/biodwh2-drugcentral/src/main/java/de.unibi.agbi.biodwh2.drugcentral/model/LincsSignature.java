package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "struct_id1", "struct_id2", "is_parent1", "is_parent2", "cell_id", "rmsd", "rmsd_norm", "pearson",
        "euclid"
})

public final class LincsSignature {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id1")
    public String structId1;
    @JsonProperty("struct_id2")
    public String structId2;
    @JsonProperty("is_parent1")
    public String isParent1;
    @JsonProperty("is_parent2")
    public String isParent2;
    @JsonProperty("cell_id")
    public String cellId;
    @JsonProperty("rmsd")
    public String rmsd;
    @JsonProperty("rmsd_norm")
    public String rmsdNorm;
    @JsonProperty("pearson")
    public String pearson;
    @JsonProperty("euclid")
    public String euclid;
}
