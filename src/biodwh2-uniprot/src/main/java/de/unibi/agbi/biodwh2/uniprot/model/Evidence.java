package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes the evidence for an annotation. No flat file equivalent.
 */
public class Evidence {
    public Source source;
    public ImportedFrom importedFrom;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public Integer key;
}
