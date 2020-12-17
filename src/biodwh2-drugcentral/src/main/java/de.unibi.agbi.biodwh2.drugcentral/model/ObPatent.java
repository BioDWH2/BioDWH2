package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "appl_type", "appl_no", "product_no", "patent_no", "patent_expire_date", "drug_substance_flag",
        "drug_product_flag", "patent_use_code", "delist_flag"
})
@NodeLabel("OrangeBookPatent")
public final class ObPatent {
    @JsonProperty("id")
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
    @JsonProperty("patent_no")
    @GraphProperty("patent_no")
    public String patentNo;
    @JsonProperty("patent_expire_date")
    @GraphProperty("patent_expire_date")
    public String patentExpireDate;
    @JsonProperty("drug_substance_flag")
    @GraphBooleanProperty(value = "drug_substance_flag", truthValue = "Y")
    public String drugSubstanceFlag;
    @JsonProperty("drug_product_flag")
    @GraphBooleanProperty(value = "drug_product_flag", truthValue = "Y")
    public String drugProductFlag;
    @JsonProperty("patent_use_code")
    @GraphProperty("patent_use_code")
    public String patentUseCode;
    @JsonProperty("delist_flag")
    @GraphBooleanProperty(value = "delist_flag", truthValue = "Y")
    public String delistFlag;
}
