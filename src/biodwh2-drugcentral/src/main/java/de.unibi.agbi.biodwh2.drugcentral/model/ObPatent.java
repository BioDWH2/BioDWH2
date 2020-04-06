package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {
        "id", "appl_type", "appl_no", "product_no", "parent_no", "patent_expire_date", "drug_substance_flag",
        "drug_product_flag", "patent_use_code", "delist_flag"
})
@NodeLabels({"ObPatent"})
public final class ObPatent {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("appl_type")
    @GraphProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    @GraphProperty("appl_no")
    public String applNo;
    @JsonProperty("product_no")
    @GraphProperty("product_no")
    public String productNo;
    @JsonProperty("parent_no")
    @GraphProperty("parent_no")
    public String parentNo;
    @JsonProperty("patent_expire_date")
    @GraphProperty("patent_expire_date")
    public String patentExpireDate;
    @JsonProperty("drug_substance_flag")
    @GraphProperty("drug_substance_flag")
    public String drugSubstanceFlag;
    @JsonProperty("drug_product_flag")
    @GraphProperty("drug_product_flag")
    public String drugProductFlag;
    @JsonProperty("patent_use_code")
    @GraphProperty("patent_use_code")
    public String patentUseCode;
    @JsonProperty("delist_flag")
    @GraphProperty("delist_flag")
    public String delistFlag;
}
