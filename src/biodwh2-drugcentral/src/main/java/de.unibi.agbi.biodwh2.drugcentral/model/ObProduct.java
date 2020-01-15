package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "ingredient", "tradeName", "applicant", "strength", "applType", "applNo",
"approvalDate", "rld", "applicantFullName", "doseForm", "route", "productNo"})

public final class ObProduct {
    @JsonProperty("id")
    public String id;
    @JsonProperty("ingredient")
    public String ingredient;
    @JsonProperty("tradeName")
    public String tradeName;
    @JsonProperty("applicant")
    public String applicant;
    @JsonProperty("strength")
    public String strength;
    @JsonProperty("applType")
    public String applType;
    @JsonProperty("applNo")
    public String applNo;
    @JsonProperty("approvalDate")
    public String approvalDate;
    @JsonProperty("rld")
    public String rld;
    @JsonProperty("applicantFullName")
    public String applicantFullName;
    @JsonProperty("doseForm")
    public String doseForm;
    @JsonProperty("route")
    public String route;
    @JsonProperty("productNo")
    public String productNo;
}
