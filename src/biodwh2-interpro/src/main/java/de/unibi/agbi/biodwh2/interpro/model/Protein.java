package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Protein {
    @JacksonXmlProperty(isAttribute = true)
    public String id;
}
