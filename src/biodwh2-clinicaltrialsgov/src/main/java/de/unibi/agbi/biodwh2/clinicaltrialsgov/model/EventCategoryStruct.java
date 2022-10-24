package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class EventCategoryStruct {
    @JsonProperty(required = true)
    public String title;
    @JsonProperty(value = "event_list", required = true)
    public EventCategoryStruct.EventList eventList;

    public static class EventList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<EventStruct> event;
    }
}
