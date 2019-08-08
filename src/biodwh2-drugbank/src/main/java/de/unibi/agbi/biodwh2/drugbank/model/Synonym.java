package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public final class Synonym {
    @JacksonXmlProperty(isAttribute = true)
    public String language;
    @JacksonXmlProperty(isAttribute = true)
    public String coder;
    @JacksonXmlText
    public String value;
}
