package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Concept {
    public String namespace;
    public String name;
    public String code;
    public String status;
    @JsonProperty("property")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Property> properties;
    @JsonProperty("synonym")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Synonym> synonyms;
}
