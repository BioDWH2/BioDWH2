package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ADR_ID", "ADR_TERM", "DRUG_ID", "DRUG_NAME", "PUBCHEM_ID"})
public class DrugADREntry {
    @JsonProperty("ADR_ID")
    public String adrId;
    @JsonProperty("ADR_TERM")
    public String adrTerm;
    @JsonProperty("DRUG_ID")
    public String drugId;
    @JsonProperty("DRUG_NAME")
    public String drugName;
    @JsonProperty("PUBCHEM_ID")
    public String pubchemID;
}
