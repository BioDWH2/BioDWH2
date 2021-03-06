package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code", "description"})
public final class ObExclusivityCode {
    @JsonProperty("code")
    public String code;
    @JsonProperty("description")
    public String description;
}
