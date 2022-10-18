package de.unibi.agbi.biodwh2.ttd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"BiomarkerID", "Biomarker_Name", "Diseasename", "ICD11", "ICD10", "ICD9"})
public class BiomarkerDisease {
    @JsonProperty("BiomarkerID")
    public String biomarkerId;
    @JsonProperty("Biomarker_Name")
    public String biomarkerName;
    @JsonProperty("Diseasename")
    public String diseaseName;
    @JsonProperty("ICD11")
    public String icd11;
    @JsonProperty("ICD10")
    public String icd10;
    @JsonProperty("ICD9")
    public String icd9;
}
