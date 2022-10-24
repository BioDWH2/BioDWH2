package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class EventStruct {
    @JsonProperty(value = "sub_title")
    public VocabTermStruct subTitle;
    public EventAssessmentEnum assessment;
    public String description;
    @JsonProperty(required = true)
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<EventCountsStruct> counts;
}
