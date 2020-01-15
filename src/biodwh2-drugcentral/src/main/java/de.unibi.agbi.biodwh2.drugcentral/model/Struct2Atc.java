package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"structId", "atcCode", "id"})

public final class Struct2Atc {
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("atcCode")
    public String atcCode;
    @JsonProperty("id")
    public String id;
}
