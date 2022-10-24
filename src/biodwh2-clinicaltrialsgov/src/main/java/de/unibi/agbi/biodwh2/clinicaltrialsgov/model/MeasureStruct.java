package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class MeasureStruct {
    @JsonProperty(required = true)
    public String title;
    public String description;
    public String population;
    public String units;
    public MeasureParamEnum param;
    public String dispersion;
    @JsonProperty(value = "units_analyzed")
    public String unitsAnalyzed;
    @JsonProperty(value = "analyzed_list")
    public MeasureStruct.AnalyzedList analyzedList;
    @JsonProperty(value = "class_list")
    public MeasureStruct.ClassList classList;

    public static class AnalyzedList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureAnalyzedStruct> analyzed;
    }

    public static class ClassList {
        @JsonProperty(value = "class", required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureClassStruct> clazz;
    }
}
