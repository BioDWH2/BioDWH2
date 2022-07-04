package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"clinical_trial_id", "reference_id"})
public class ClinicalTrialRef {
    @JsonProperty("clinical_trial_id")
    public Long clinicalTrialId;
    @JsonProperty("reference_id")
    public Long referenceId;
}
