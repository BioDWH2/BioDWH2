package de.unibi.agbi.biodwh2.uniprot.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Describes the source organism.
 */
public class Organism {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<OrganismName> name;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<DbReference> dbReference;
    public Organism.Lineage lineage;
    @JacksonXmlProperty(isAttribute = true)
    public String evidence;

    public static class Lineage {
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<String> taxon;
    }
}
