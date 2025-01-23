package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class BioSource extends UtilityClass {
    @GraphProperty("name")
    public String name;
    @GraphProperty("display_name")
    public String displayName;
    @GraphProperty("standard_name")
    public String standardName;
    public ResourceRef xref;
    public ResourceRef tissue;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] cellType;
}
