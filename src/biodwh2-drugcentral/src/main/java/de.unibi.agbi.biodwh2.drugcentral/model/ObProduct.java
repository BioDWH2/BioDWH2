package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "ingredient", "trade_name", "applicant", "strength", "appl_type", "appl_no", "te_code", "approval_date",
        "rld", "type", "applicant_full_name", "dose_form", "route", "product_no"
})
@GraphNodeLabel("OrangeBookProduct")
public final class ObProduct {
    @JsonProperty("id")
    @GraphProperty("id")
    public Integer id;
    @JsonProperty("ingredient")
    @GraphProperty("ingredient")
    public String ingredient;
    @JsonProperty("trade_name")
    @GraphProperty("trade_name")
    public String tradeName;
    @JsonProperty("applicant")
    @GraphProperty("applicant")
    public String applicant;
    @JsonProperty("strength")
    @GraphProperty("strength")
    public String strength;
    @JsonProperty("appl_type")
    @GraphProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    @GraphProperty("appl_no")
    public String applNo;
    @JsonProperty("te_code")
    @GraphProperty("te_code")
    public String teCode;
    @JsonProperty("approval_date")
    @GraphProperty("approval_date")
    public String approvalDate;
    @JsonProperty("rld")
    @GraphProperty("rld")
    public String rld;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("applicant_full_name")
    @GraphProperty("applicant_full_name")
    public String applicantFullName;
    @JsonProperty("dose_form")
    @GraphProperty("dose_form")
    public String doseForm;
    @JsonProperty("route")
    @GraphProperty("route")
    public String route;
    @JsonProperty("product_no")
    @GraphProperty("product_no")
    public String productNo;
}
