package de.unibi.agbi.biodwh2.trrust.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Annotation {
    @JacksonXmlProperty
    public String id;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Infon> infon;
    public String text;
}
