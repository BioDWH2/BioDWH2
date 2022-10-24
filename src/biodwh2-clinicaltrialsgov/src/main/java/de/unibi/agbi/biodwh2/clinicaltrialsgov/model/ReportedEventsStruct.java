package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class ReportedEventsStruct {
    @JsonProperty(value = "time_frame")
    public String timeFrame;
    public String desc;
    @JsonProperty(value = "group_list", required = true)
    public ReportedEventsStruct.GroupList groupList;
    @JsonProperty(value = "serious_events")
    public EventsStruct seriousEvents;
    @JsonProperty(value = "other_events")
    public EventsStruct otherEvents;

    public static class GroupList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<GroupStruct> group;
    }
}
