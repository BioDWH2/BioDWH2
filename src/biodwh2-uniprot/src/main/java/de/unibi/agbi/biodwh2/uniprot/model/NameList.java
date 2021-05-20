package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class NameList {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<PersonOrConsortium> person;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<PersonOrConsortium> consortium;
}
