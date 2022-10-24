package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class EventsStruct {
    @JsonProperty(value = "frequency_threshold")
    public String frequencyThreshold;
    @JsonProperty(value = "default_vocab")
    public String defaultVocab;
    @JsonProperty(value = "default_assessment")
    public EventAssessmentEnum defaultAssessment;
    @JsonProperty(value = "category_list", required = true)
    public EventsStruct.CategoryList categoryList;

    public static class CategoryList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<EventCategoryStruct> category;
    }
}
