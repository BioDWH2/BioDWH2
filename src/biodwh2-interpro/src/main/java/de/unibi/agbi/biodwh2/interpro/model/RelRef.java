package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RelRef {
    @JacksonXmlProperty(localName = "ipr_ref", isAttribute = true)
    public String iprRef;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
}
