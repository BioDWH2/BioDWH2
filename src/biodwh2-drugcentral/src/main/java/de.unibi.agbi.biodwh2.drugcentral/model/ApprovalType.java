package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "description"})

public final class ApprovalType {
    @JsonProperty("id")
    public String id;
    @JsonProperty("description")
    public String description;
}
