package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PendingResultsStruct {
    public VariableDateStruct returned;
    public VariableDateStruct submitted;
    @JsonProperty("submission_canceled")
    public VariableDateStruct submissionCanceled;
}
