package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Describes a molecule by name or unique identifier.
 */
public class Molecule {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public String id;
}
