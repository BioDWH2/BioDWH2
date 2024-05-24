package de.unibi.agbi.biodwh2.nsides.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "drug_rxnorm_id", "drug_concept_name", "condition_meddra_id", "condition_concept_name", "A", "B", "C", "D",
        "PRR", "PRR_error", "mean_reporting_frequency"
})
public class OffsidesEntry {
    @JsonProperty("drug_rxnorm_id")
    public String drugRxnormId;
    @JsonProperty("drug_concept_name")
    public String drugConceptName;
    @JsonProperty("condition_meddra_id")
    public String conditionMeddraId;
    @JsonProperty("condition_concept_name")
    public String conditionConceptName;
    @JsonProperty("A")
    public String a;
    @JsonProperty("B")
    public String b;
    @JsonProperty("C")
    public String c;
    @JsonProperty("D")
    public String d;
    @JsonProperty("PRR")
    public String prr;
    @JsonProperty("PRR_error")
    public String prrError;
    @JsonProperty("mean_reporting_frequency")
    public String meanReportingFrequency;
}
