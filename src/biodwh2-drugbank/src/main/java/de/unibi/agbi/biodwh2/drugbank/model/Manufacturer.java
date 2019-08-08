package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public final class Manufacturer {
    @JacksonXmlProperty(isAttribute = true)
    public boolean generic;
    @JacksonXmlProperty(isAttribute = true)
    public String url;
    @JacksonXmlText
    public String value;
}
