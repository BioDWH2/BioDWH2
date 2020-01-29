package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "ingredient", "trade_name", "applicant", "strength", "appl_type", "appl_no", "te_code", "approval_date", "rld",
        "applicant_full_name", "dose_form", "route", "product_no"
})

public final class ObProduct {
    @JsonProperty("id")
    public String id;
    @JsonProperty("ingredient")
    public String ingredient;
    @JsonProperty("trade_name")
    public String tradeName;
    @JsonProperty("applicant")
    public String applicant;
    @JsonProperty("strength")
    public String strength;
    @JsonProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    public String applNo;
    @JsonProperty("te_code")
    public String teCode;
    @JsonProperty("approval_date")
    public String approvalDate;
    @JsonProperty("rld")
    public String rld;
    @JsonProperty("type")
    public String type;
    @JsonProperty("applicant_full_name")
    public String applicantFullName;
    @JsonProperty("doseForm")
    public String doseForm;
    @JsonProperty("route")
    public String route;
    @JsonProperty("product_no")
    public String productNo;
}
