package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class ClinicalResultsStruct {
    @JsonProperty(value = "participant_flow", required = true)
    public ParticipantFlowStruct participantFlow;
    @JsonProperty(required = true)
    public BaselineStruct baseline;
    @JsonProperty(value = "outcome_list", required = true)
    public ClinicalResultsStruct.OutcomeList outcomeList;
    @JsonProperty(value = "reported_events")
    public ReportedEventsStruct reportedEvents;
    @JsonProperty(value = "certain_agreements")
    public CertainAgreementsStruct certainAgreements;
    @JsonProperty(value = "limitations_and_caveats")
    public String limitationsAndCaveats;
    @JsonProperty(value = "point_of_contact")
    public PointOfContactStruct pointOfContact;

    public static class OutcomeList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<ResultsOutcomeStruct> outcome;
    }
}
