package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KnownDrugAggregated {
    @JsonProperty("drugId")
    public String drugId;
    @JsonProperty("targetId")
    public String targetId;
    @JsonProperty("diseaseId")
    public String diseaseId;
    @JsonProperty("phase")
    public Integer phase;
    @JsonProperty("status")
    public String status;
    @JsonProperty("targetName")
    public String targetName;
    @JsonProperty("mechanismOfAction")
    public String mechanismOfAction;
    @JsonProperty("drugType")
    public String drugType;
    @JsonProperty("prefName")
    public String prefName;
    @JsonProperty("synonyms")
    public String[] synonyms;
    @JsonProperty("tradeNames")
    public String[] tradeNames;
    @JsonProperty("targetClass")
    public String[] targetClass;
    @JsonProperty("approvedSymbol")
    public String approvedSymbol;
    @JsonProperty("approvedName")
    public String approvedName;
    @JsonProperty("label")
    public String label;
    @JsonProperty("ancestors")
    public String[] ancestors;
    @JsonProperty("urls")
    public Url[] urls;
}
