package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class MeasureClassStruct {
    public String title;
    @JsonProperty(value = "analyzed_list")
    public MeasureClassStruct.AnalyzedList analyzedList;
    @JsonProperty(value = "category_list")
    public MeasureClassStruct.CategoryList categoryList;

    public static class AnalyzedList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureAnalyzedStruct> analyzed;
    }

    public static class CategoryList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureCategoryStruct> category;
    }
}
