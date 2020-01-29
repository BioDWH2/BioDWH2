package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "appl_type", "appl_no", "product_no", "parent_no", "patent_expire_date", "drug_substance_flag",
        "drug_product_flag", "patent_use_code", "delist_flag"
})

public final class ObPatent {
    @JsonProperty("id")
    public String id;
    @JsonProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    public String applNo;
    @JsonProperty("product_no")
    public String productNo;
    @JsonProperty("parent_no")
    public String parentNo;
    @JsonProperty("patent_expire_date")
    public String patentExpireDate;
    @JsonProperty("drug_substance_flag")
    public String drugSubstanceFlag;
    @JsonProperty("drug_product_flag")
    public String drugProductFlag;
    @JsonProperty("patent_use_code")
    public String patentUseCode;
    @JsonProperty("delist_flag")
    public String delistFlag;
}
