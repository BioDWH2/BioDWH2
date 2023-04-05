package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "DRUG_ID", "DRUG_NAME", "DrugBank_ID", "ADR_ID", "ADR_TERM", "ADR_Severity_Grade_FAERS", "ADR_Frequency_FAERS"
})
public class DrugADRQuantificationEntry {
    @JsonProperty("DRUG_ID")
    public String drugId;
    @JsonProperty("DRUG_NAME")
    public String drugName;
    @JsonProperty("DrugBank_ID")
    public String drugBankId;
    @JsonProperty("ADR_ID")
    public String adrId;
    @JsonProperty("ADR_TERM")
    public String adrTerm;
    @JsonProperty("ADR_Severity_Grade_FAERS")
    public String adrSeverityGradeFAERS;
    @JsonProperty("ADR_Frequency_FAERS")
    public String adrFrequencyFAERS;
}
