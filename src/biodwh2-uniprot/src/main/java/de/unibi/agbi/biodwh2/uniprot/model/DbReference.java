package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes a database cross-reference. Equivalent to the flat file DR-line.
 */
public class DbReference {
    public Molecule molecule;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Property> property;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
