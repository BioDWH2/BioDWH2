package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Classification {
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(localName = "class_type", isAttribute = true)
    public String classType;
    public String category;
    public String description;
}
