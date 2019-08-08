package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public final class Cost {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public String currency;
}
