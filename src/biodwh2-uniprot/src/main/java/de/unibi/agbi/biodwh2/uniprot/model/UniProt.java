package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes a collection of UniProtKB entries.
 * <p>
 * https://www.uniprot.org/docs/uniprot.xsd
 */
@JsonIgnoreProperties(value = {"schemaLocation"})
public class UniProt {
    public String copyright;
    @JacksonXmlProperty(localName = "entry")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Entry> entries;
}
