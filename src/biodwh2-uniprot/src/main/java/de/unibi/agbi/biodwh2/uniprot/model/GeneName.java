package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Describes different types of gene designations. Equivalent to the flat file GN-line.
 */
public class GeneName {
    @JacksonXmlText
    public String value;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
}
