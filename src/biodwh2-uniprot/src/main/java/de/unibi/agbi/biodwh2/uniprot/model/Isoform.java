package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

/**
 * Describes isoforms in 'alternative products' annotations.
 */
public class Isoform {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> id;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Isoform.Name> name;
    public Isoform.Sequence sequence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<EvidencedString> text;

    public static class Name {
        @JacksonXmlText
        public String value;
        @JacksonXmlProperty(isAttribute = true)
        public String evidence;
    }

    public static class Sequence {
        @JacksonXmlProperty(isAttribute = true)
        public String type;
        @JacksonXmlProperty(isAttribute = true)
        public String ref;
    }
}
