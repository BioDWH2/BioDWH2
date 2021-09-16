package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes a chemical reaction.
 */
public class Reaction {
    public String text;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<DbReference> dbReference;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
