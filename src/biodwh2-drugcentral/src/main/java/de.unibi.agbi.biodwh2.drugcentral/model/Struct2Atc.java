package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"struct_id", "atc_code", "id"})

public final class Struct2Atc {
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("atc_code")
    public String atcCode;
    @JsonProperty("id")
    public String id;
}
