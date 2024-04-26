package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class SequenceVariantMeasurement {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> condition;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("indication_types")
    public List<String> indicationTypes;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Integer> reference;
}
