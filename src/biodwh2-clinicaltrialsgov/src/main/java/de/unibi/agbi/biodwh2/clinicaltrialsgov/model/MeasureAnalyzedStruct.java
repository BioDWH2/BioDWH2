package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class MeasureAnalyzedStruct {
    @JsonProperty(required = true)
    public String units;
    @JsonProperty(required = true)
    public String scope;
    @JsonProperty(value = "count_list", required = true)
    public MeasureAnalyzedStruct.CountList countList;

    public static class CountList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasureCountStruct> count;
    }
}
