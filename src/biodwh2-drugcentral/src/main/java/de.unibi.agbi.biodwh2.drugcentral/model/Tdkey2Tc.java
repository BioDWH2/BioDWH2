package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "tdkey_id", "component_id"})
public final class Tdkey2Tc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("tdkey_id")
    public String tdKeyId;
    @JsonProperty("component_id")
    public Integer componentId;
}
