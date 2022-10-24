package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class InterventionStruct {
    @JsonProperty(value = "intervention_type", required = true)
    public InterventionTypeEnum interventionType;
    @JsonProperty(value = "intervention_name", required = true)
    public String interventionName;
    public String description;
    @JsonProperty(value = "arm_group_label")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> armGroupLabel;
    @JsonProperty(value = "other_name")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> otherName;
}
