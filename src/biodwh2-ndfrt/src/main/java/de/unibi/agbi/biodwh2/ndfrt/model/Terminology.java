package de.unibi.agbi.biodwh2.ndfrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Terminology {
    @JsonProperty("ref_by")
    public String refBy;
    @JsonProperty("if_exists_action")
    public String ifExistsAction;
    @JsonProperty("associationDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Association> associations;
    @JsonProperty("conceptDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Concept> concepts;
    @JsonProperty("kindDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Kind> kinds;
    @JsonProperty("namespaceDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Namespace> namespaces;
    @JsonProperty("propertyDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Property> properties;
    @JsonProperty("qualifierDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Qualifier> qualifiers;
    @JsonProperty("roleDef")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Role> roles;
}
