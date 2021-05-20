package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Sequence {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public int length;
    @JacksonXmlProperty(isAttribute = true)
    public int mass;
    @JacksonXmlProperty(isAttribute = true)
    public String checksum;
    @JacksonXmlProperty(isAttribute = true)
    public String modified;
    @JacksonXmlProperty(isAttribute = true)
    public int version;
    @JacksonXmlProperty(isAttribute = true)
    public Boolean precursor;
    @JacksonXmlProperty(isAttribute = true)
    public String fragment;
}
