package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class EvidencedString {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
