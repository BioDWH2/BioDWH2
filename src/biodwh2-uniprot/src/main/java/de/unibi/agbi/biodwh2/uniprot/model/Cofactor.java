package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes a cofactor.
 */
public class Cofactor {
    public String name;
    public DbReference dbReference;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
