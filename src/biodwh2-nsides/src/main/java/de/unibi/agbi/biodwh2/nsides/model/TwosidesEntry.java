package de.unibi.agbi.biodwh2.nsides.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "drug_1_rxnorm_id", "drug_1_concept_name", "drug_2_rxnorm_id", "drug_2_concept_name", "condition_meddra_id",
        "condition_concept_name", "A", "B", "C", "D", "PRR", "PRR_error", "mean_reporting_frequency"
})
public class TwosidesEntry {
    @JsonProperty("drug_1_rxnorm_id")
    public String drug1RxnormId;
    @JsonProperty("drug_1_concept_name")
    public String drug1ConceptName;
    @JsonProperty("drug_2_rxnorm_id")
    public String drug2RxnormId;
    @JsonProperty("drug_2_concept_name")
    public String drug2ConceptName;
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
