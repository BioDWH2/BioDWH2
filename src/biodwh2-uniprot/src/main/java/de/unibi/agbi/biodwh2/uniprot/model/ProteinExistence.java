package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Describes the evidence for the protein's existence. Equivalent to the flat file PE-line.
 */
public class ProteinExistence {
    @JacksonXmlProperty(isAttribute = true)
    public String type;
}
