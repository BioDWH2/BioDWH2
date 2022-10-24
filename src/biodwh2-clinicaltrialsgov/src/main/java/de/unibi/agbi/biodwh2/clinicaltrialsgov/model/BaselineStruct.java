package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class BaselineStruct {
    public String population;
    @JsonProperty(value = "group_list", required = true)
    public BaselineStruct.GroupList groupList;
    @JsonProperty(value = "analyzed_list")
    public BaselineStruct.AnalyzedList analyzedList;
    @JsonProperty(value = "measure_list")
    public BaselineStruct.MeasureList measureList;

    public static class AnalyzedList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureAnalyzedStruct> analyzed;
    }

    public static class GroupList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<GroupStruct> group;
    }

    public static class MeasureList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureStruct> measure;
    }
}
