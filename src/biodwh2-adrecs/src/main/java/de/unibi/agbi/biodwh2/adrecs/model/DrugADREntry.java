package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "DRUG_ID", "DRUG_NAME", "PubChem_CID", "ADR_ID", "ADR_TERM", "ADRECS_ID", "ADR_Classification", "MEDDRA_CODE"
})
public class DrugADREntry {
    @JsonProperty("DRUG_ID")
    public String drugId;
    @JsonProperty("DRUG_NAME")
    public String drugName;
    @JsonProperty("PubChem_CID")
    public String pubchemCID;
    @JsonProperty("ADR_ID")
    public String adrId;
    @JsonProperty("ADR_TERM")
    public String adrTerm;
    @JsonProperty("ADRECS_ID")
    public String adrecsId;
    @JsonProperty("ADR_Classification")
    public String adrClassification;
    @JsonProperty("MEDDRA_CODE")
    public String meddraCode;
}
