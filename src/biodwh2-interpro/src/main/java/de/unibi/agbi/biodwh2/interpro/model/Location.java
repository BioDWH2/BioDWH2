package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Location {
    @JacksonXmlProperty(isAttribute = true)
    public String pages;
    @JacksonXmlProperty(isAttribute = true)
    public String volume;
    @JacksonXmlProperty(isAttribute = true)
    public String issue;
}
