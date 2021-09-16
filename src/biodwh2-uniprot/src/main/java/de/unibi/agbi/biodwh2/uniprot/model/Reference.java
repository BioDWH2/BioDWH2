package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes a citation and a summary of its content. Equivalent to the flat file RN-, RP-, RC-, RX-, RG-, RA-, RT- and
 * RL-lines.
 */
public class Reference {
    public Citation citation;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> scope;
    public SourceData source;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;
    @JacksonXmlProperty(isAttribute = true)
    public String key;
}
