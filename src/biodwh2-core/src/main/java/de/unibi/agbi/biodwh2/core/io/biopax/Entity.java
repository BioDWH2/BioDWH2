package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public abstract class Entity {
    @JacksonXmlProperty(isAttribute = true)
    public String about;
    @JacksonXmlElementWrapper(useWrapping = false)
    public String[] comment;
    public ResourceRef dataSource;
    public ResourceRef evidence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] xref;
    public String name;
    public String displayName;
    public String standardName;
}
