package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public final class Terminology {
    public Namespace namespace;
    @JsonProperty("referencedNamespace")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Namespace> referencedNamespaces;
    @SuppressWarnings("SpellCheckingInspection")
    @JsonProperty("proptype")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<PropertyType> propertyTypes;
    @SuppressWarnings("SpellCheckingInspection")
    @JsonProperty("assntype")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<AssociationType> associationTypes;
    @SuppressWarnings("SpellCheckingInspection")
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
