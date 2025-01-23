package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public abstract class EntityReference extends UtilityClass {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] entityFeature;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] memberEntityReference;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] entityReferenceType;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] evidence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] xref;
    @JacksonXmlElementWrapper(useWrapping = false)
    @GraphProperty("name")
    public String[] name;
    @GraphProperty("display_name")
    public String displayName;
    @GraphProperty("standard_name")
    public String standardName;
}
