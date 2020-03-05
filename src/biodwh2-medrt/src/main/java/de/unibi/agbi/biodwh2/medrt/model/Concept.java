package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

import java.util.List;

@NodeLabels({"Concept"})
public class Concept {
    public String namespace;
    @GraphProperty("name")
    public String name;
    @GraphProperty("code")
    public String code;
    @GraphProperty("status")
    public String status;
    @JsonProperty("property")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Property> properties;
    @JsonProperty("synonym")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Synonym> synonyms;
}
