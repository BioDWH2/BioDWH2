package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "code", "allow_cycles", "description"
})
public class RelationType {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("code")
    public String code;
    @JsonProperty("allow_cycles")
    public String allowCycles;
    @JsonProperty("description")
    public String description;
}
