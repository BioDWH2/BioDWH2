package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Indicates whether the name of a plasmid is known or unknown.
 */
public class Status {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public String status;
}
