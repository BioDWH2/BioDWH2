package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * Protein in a specific state. For base protein definitions {@link ProteinReference} is used.
 */
public class Protein extends PhysicalEntity {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] entityReference;
}
