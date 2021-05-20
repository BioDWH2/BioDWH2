package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes the source of the data using a database cross-reference (or a 'ref' attribute when the source cannot be
 * found in a public data source, such as PubMed, and is cited only within the UniProtKB entry).
 */
public class Source {
    public DbReference dbReference;
    @JacksonXmlProperty(isAttribute = true)
    public Integer ref;
}
