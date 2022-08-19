package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"DRUG_ID", "ADR_ID", "ADR_Frequency_FAERS", "ADR_Severity_Grade_FAERS"})
public class DrugADREntry {
    @JsonProperty("DRUG_ID")
    public String drugId;
    @JsonProperty("ADR_ID")
    public String adrId;
    @JsonProperty("ADR_Frequency_FAERS")
    public String adrFrequencyFAERS;
    @JsonProperty("ADR_Severity_Grade_FAERS")
    public String adrSeverityGradeFAERS;
}
