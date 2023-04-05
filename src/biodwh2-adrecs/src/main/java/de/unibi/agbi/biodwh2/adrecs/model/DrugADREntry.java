package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"DRUG_ID", "DRUG_NAME", "ADR_ID", "ADR_TERM"})
public class DrugADREntry {
    @JsonProperty("DRUG_ID")
    public String drugId;
    @JsonProperty("DRUG_NAME")
    public String drugName;
    @JsonProperty("ADR_ID")
    public String adrId;
    @JsonProperty("ADR_TERM")
    public String adrTerm;
}
