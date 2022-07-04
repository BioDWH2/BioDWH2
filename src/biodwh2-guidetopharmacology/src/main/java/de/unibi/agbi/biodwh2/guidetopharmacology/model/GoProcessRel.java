package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"parent_id", "child_id"})
public class GoProcessRel {
    @JsonProperty("parent_id")
    public Long parentId;
    @JsonProperty("child_id")
    public Long childId;
}
