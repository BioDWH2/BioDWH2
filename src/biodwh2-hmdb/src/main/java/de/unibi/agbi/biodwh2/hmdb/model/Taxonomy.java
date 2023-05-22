package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Taxonomy {
    public String description;
    @JsonProperty("direct_parent")
    public String directParent;
    public String kingdom;
    @JsonProperty("super_class")
    public String superClass;
    @JsonProperty("class")
    public String class_;
    @JsonProperty("sub_class")
    public String subClass;
    @JsonProperty("molecular_framework")
    public String molecularFramework;
    @JacksonXmlElementWrapper(localName = "alternative_parents")
    public List<String> alternativeParents;
    @JacksonXmlElementWrapper(localName = "substituents")
    public List<String> substituents;
    @JacksonXmlElementWrapper(localName = "external_descriptors")
    public List<String> externalDescriptors;
}
