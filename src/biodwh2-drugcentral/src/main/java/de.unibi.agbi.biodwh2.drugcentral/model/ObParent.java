package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "applType", "applNo", "productNo", "parentNo", "patentExpireDate", "drugSubstanceFlag",
"drugProductFlag", "patentUseCode", "delistFlag"})

public final class ObParent {
    @JsonProperty("id")
    public String id;
    @JsonProperty("applType")
    public String applType;
    @JsonProperty("applNo")
    public String applNo;
    @JsonProperty("productNo")
    public String productNo;
    @JsonProperty("parentNo")
    public String parentNo;
    @JsonProperty("patentExpireDate")
    public String patentExpireDate;
    @JsonProperty("drugSubstanceFlag")
    public String drugSubstanceFlag;
    @JsonProperty("drugProductFlag")
    public String drugProductFlag;
    @JsonProperty("patentUseCode")
    public String patentUseCode;
    @JsonProperty("delistFlag")
    public String delistFlag;
}
