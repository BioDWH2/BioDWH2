package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public final class Classification {
    public String description;
    @JsonProperty("direct-parent")
    public String directParent;
    public String kingdom;
    public String superclass;
    @JsonProperty("class")
    public String class_;
    public String subclass;
    @JsonProperty("alternative-parent")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> alternativeParents;
    @JsonProperty("substituent")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> substituents;
}
