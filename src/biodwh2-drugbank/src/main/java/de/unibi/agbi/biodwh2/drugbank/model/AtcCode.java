package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.ArrayList;

public final class AtcCode {
    public static class Level {
        @JacksonXmlProperty(isAttribute = true)
        public String code;
        @JacksonXmlText
        public String value;
    }

    @JacksonXmlProperty(isAttribute = true)
    public String code;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<Level> level;
}
