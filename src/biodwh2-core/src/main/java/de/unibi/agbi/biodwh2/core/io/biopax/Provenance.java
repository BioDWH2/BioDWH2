package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class Provenance extends UtilityClass {
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("name")
    public String[] name;
    @GraphProperty("display_name")
    public String displayName;
    @GraphProperty("standard_name")
    public String standardName;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] xref;
}
