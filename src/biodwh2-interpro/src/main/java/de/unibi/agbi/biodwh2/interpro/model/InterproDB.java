package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class InterproDB {
    public Release release;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Interpro> interpro;
    @JacksonXmlProperty(localName = "deleted_entries")
    public List<DelRef> deletedEntries;
}
