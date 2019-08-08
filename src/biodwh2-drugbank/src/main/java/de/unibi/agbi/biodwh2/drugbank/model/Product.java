package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Product {
    public String name;
    public String labeller;
    @JsonProperty("ndc-id")
    public String ndcId;
    @JsonProperty("ndc-product-code")
    public String ndcProductCode;
    @JsonProperty("dpd-id")
    public String dpdId;
    @JsonProperty("ema-product-code")
    public String emaProductCode;
    @JsonProperty("ema-ma-number")
    public String emaMaNumber;
    @JsonProperty("started-marketing-on")
    public String startedMarketingOn;
    @JsonProperty("ended-marketing-on")
    public String endedMarketingOn;
    @JsonProperty("dosage-form")
    public String dosageForm;
    public String strength;
    public String route;
    @JsonProperty("fda-application-number")
    public String fdaApplicationNumber;
    public boolean generic;
    @JsonProperty("over-the-counter")
    public boolean overTheCounter;
    public boolean approved;
    public ProductCountry country;
    public ProductSource source;
}
