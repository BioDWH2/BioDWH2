package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Protein {
    @JacksonXmlProperty(isAttribute = true)
    public String about;
    public ResourceRef evidence;
    public String displayName;
    public ResourceRef entityReference;
    public String name;
    public String comment;
    public ResourceRef dataSource;
}
