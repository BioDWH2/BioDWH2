package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "drugClassId"})

public final class Struct2DrgClass {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("drugCalssId")
    public String drugClassId;
}
