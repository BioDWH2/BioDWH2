package de.unibi.agbi.biodwh2.trrust.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Document {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Passage> passage;
}
