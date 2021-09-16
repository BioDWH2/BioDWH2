package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PersonOrConsortium {
    @JacksonXmlProperty(isAttribute = true)
    public String name;
}
