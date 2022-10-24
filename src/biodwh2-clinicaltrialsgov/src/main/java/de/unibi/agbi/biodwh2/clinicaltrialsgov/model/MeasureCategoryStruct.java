package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class MeasureCategoryStruct {
    public String title;
    @JsonProperty(value = "measurement_list")
    public MeasureCategoryStruct.MeasurementList measurementList;

    public static class MeasurementList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MeasurementStruct> measurement;
    }
}
