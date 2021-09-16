package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes a physiological reaction.
 */
public class PhysiologicalReaction {
    public DbReference dbReference;
    @JacksonXmlProperty(isAttribute = true)
    public String direction;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
}
