package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes a sequence location as either a range with a begin and end or as a position. The 'sequence' attribute is
 * only used when the location is not on the canonical sequence displayed in the current entry.
 */
public class Location {
    public Position begin;
    public Position end;
    public Position position;
    @JacksonXmlProperty(isAttribute = true)
    public String sequence;
}
