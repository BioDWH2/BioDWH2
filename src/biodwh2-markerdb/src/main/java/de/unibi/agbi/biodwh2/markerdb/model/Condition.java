package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Condition {
    public String concentration;
    public String age;
    public String sex;
    public String biofluid;
    public String condition;
    @JsonProperty("indication_type")
    public String indicationType;
    public String citation;
    @JsonProperty("biomarker_catogory")
    public String biomarkerCategory;
    public String name;
}
