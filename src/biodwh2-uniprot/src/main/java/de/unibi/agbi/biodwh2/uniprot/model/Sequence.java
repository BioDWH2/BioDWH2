package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Sequence {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public Integer length;
    @JacksonXmlProperty(isAttribute = true)
    public Integer mass;
    @JacksonXmlProperty(isAttribute = true)
    public String checksum;
    @JacksonXmlProperty(isAttribute = true)
    public String modified;
    @JacksonXmlProperty(isAttribute = true)
    public Integer version;
    @JacksonXmlProperty(isAttribute = true)
    public Boolean precursor;
    @JacksonXmlProperty(isAttribute = true)
    public String fragment;
}
