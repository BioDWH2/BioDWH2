package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes different types of sequence annotations. Equivalent to the flat file FT-line.
 */
public class Feature {
    public String original;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> variation;
    public Location location;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String description;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
    @JacksonXmlProperty(isAttribute = true)
    public String ref;
}
