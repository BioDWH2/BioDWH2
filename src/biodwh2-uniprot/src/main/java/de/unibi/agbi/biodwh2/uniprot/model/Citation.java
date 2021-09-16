package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes different types of citations. Equivalent to the flat file RX-, RG-, RA-, RT- and RL-lines.
 */
public class Citation {
    public String title;
    public NameList editorList;
    public NameList authorList;
    public String locator;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<DbReference> dbReference;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public String date;
    @JacksonXmlProperty(isAttribute = true)
    public String name;
    @JacksonXmlProperty(isAttribute = true)
    public String volume;
    @JacksonXmlProperty(isAttribute = true)
    public String first;
    @JacksonXmlProperty(isAttribute = true)
    public String last;
    @JacksonXmlProperty(isAttribute = true)
    public String publisher;
    @JacksonXmlProperty(isAttribute = true)
    public String city;
    @JacksonXmlProperty(isAttribute = true)
    public String db;
    @JacksonXmlProperty(isAttribute = true)
    public String number;
    @JacksonXmlProperty(isAttribute = true)
    public String institute;
    @JacksonXmlProperty(isAttribute = true)
    public String country;
}
