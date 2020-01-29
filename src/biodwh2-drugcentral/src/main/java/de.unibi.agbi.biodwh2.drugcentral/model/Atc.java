package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "code", "chemical_substance", "l1_code", "l1_name", "l2_code", "l2_name", "l3_code", "l3_name", "l4_code",
        "l4_name", "chemical_substance_count"
})

public final class Atc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("code")
    public String code;
    @JsonProperty("chemical_substance")
    public String chemicalSubstance;
    @JsonProperty("l1_code")
    public String l1Code;
    @JsonProperty("l1_name")
    public String l1Name;
    @JsonProperty("l2_code")
    public String l2Code;
    @JsonProperty("l2_name")
    public String l2Name;
    @JsonProperty("l3_code")
    public String l3Code;
    @JsonProperty("l3_name")
    public String l3Name;
    @JsonProperty("l4_code")
    public String l4Code;
    @JsonProperty("l4_name")
    public String l4Name;
    @JsonProperty("chemical_substance_count")
    public String chemicalSubstanceCount;
}
