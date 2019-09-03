package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Terminology {
    public Namespace namespace;
    @JsonProperty("referencedNamespace")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Namespace> referencedNamespaces;
    @JsonProperty("proptype")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<PropertyType> propertyTypes;
    @JsonProperty("assntype")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<AssociationType> associationTypes;
    @JsonProperty("qualtype")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<QualitativeType> qualitativeTypes;
    @JsonProperty("term")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Term> terms;
    @JsonProperty("concept")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Concept> concepts;
    @JsonProperty("association")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Association> associations;
}
