package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes non-nuclear gene locations (organelles and plasmids). Equivalent to the flat file OG-line.
 */
public class GeneLocation {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Status> name;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
