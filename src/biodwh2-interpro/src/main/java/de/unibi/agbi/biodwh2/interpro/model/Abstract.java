package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Abstract {
    @JacksonXmlProperty(localName = "is-llm", isAttribute = true)
    public Boolean isLLM;
    @JacksonXmlProperty(localName = "is-llm-reviewed", isAttribute = true)
    public Boolean isLLMReviewed;
    @JacksonXmlText
    public String value;
}
