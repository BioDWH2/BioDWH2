package de.unibi.agbi.biodwh2.trrust.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Passage {
    public Infon infon;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Annotation> annotation;
}
