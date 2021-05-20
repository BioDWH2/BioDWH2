package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes the type of events that cause alternative products.
 */
public class Event {
    @JacksonXmlProperty(isAttribute = true)
    public String type;
}
