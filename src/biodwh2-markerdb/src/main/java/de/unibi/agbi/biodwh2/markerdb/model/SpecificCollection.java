package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"to_s"})
public class SpecificCollection {
    public String version;
    @JsonProperty("biomarker_type")
    public String biomarkerType;
    public Biomarkers biomarkers;
}
