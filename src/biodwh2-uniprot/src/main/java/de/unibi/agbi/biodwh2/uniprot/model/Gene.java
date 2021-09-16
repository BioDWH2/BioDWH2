package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Describes a gene. Equivalent to the flat file GN-line.
 */
public class Gene {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<GeneName> name;
}
