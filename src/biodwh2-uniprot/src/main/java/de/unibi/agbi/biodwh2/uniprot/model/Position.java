package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Position {
    @JacksonXmlProperty(isAttribute = true)
    public Long position;
    @JacksonXmlProperty(isAttribute = true)
    public String status;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
