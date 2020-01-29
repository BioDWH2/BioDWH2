package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "appl_type", "product_no", "exclusivity_code", "exclusivity_date"})

public final class ObExclusivity {
    @JsonProperty("id")
    public String id;
    @JsonProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    public String applNo;
    @JsonProperty("product_no")
    public String productNo;
    @JsonProperty("exclusivity_code")
    public String exclusivityCode;
    @JsonProperty("exclusivity_date")
    public String exclusivityDate;
}
