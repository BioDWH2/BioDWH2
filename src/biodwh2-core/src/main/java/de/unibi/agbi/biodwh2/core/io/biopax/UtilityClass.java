package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public abstract class UtilityClass {
    /**
     * Sometimes used instead of rdf:about
     */
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    @GraphProperty("about")
    public String ID;
    @JacksonXmlProperty(isAttribute = true)
    @GraphProperty("about")
    public String about;
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("comment")
    public String[] comment;
}
