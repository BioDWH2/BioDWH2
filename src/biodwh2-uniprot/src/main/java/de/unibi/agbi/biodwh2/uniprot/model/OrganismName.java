package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Describes different types of source organism names.
 */
public class OrganismName {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
}
