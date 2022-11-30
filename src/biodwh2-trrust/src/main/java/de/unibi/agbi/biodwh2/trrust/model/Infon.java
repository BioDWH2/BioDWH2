package de.unibi.agbi.biodwh2.trrust.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Infon {
    @JacksonXmlProperty
    public String key;
    @JacksonXmlText
    public String value;
}
