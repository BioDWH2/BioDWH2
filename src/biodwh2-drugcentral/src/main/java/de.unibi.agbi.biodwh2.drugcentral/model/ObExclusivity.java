package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "applType", "productNo", "exclusivityCode", "exclusivityDate"})

public final class ObExclusivity {
    @JsonProperty("id")
    public String id;
    @JsonProperty("applType")
    public String applType;
    @JsonProperty("productNo")
    public String productNo;
    @JsonProperty("exclusivityCode")
    public String exclusivityCode;
    @JsonProperty("exclusivityDate")
    public String exclusivityDate;
}
