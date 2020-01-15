package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "code", "chemicalSubstance", "l1Code", "l1Name", "l2Code", "l2Name",
"l3Code", "l3Name", "l4Code", "l4Name", "chemicalSubstanceCount"})

public final class Atc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("code")
    public String code;
    @JsonProperty("chemicalSubstance")
    public String chemicalSubstance;
    @JsonProperty("l1Code")
    public String l1Code;
    @JsonProperty("l1Name")
    public String l1Name;
    @JsonProperty("l2Code")
    public String l2Code;
    @JsonProperty("l2Name")
    public String l2Name;
    @JsonProperty("l3Code")
    public String l3Code;
    @JsonProperty("l3Name")
    public String l3Name;
    @JsonProperty("l4Code")
    public String l4Code;
    @JsonProperty("l4Name")
    public String l4Name;
    @JsonProperty("chemicalSubstanceCount")
    public String chemicalSubstanceCount;
}
