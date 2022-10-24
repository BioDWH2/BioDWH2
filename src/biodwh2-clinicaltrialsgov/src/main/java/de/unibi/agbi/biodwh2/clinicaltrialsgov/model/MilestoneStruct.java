package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class MilestoneStruct {
    @JsonProperty(required = true)
    public String title;
    @JsonProperty(value = "participants_list", required = true)
    public MilestoneStruct.ParticipantsList participantsList;

    public static class ParticipantsList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<ParticipantsStruct> participants;
    }
}
