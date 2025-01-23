package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Pathway extends Entity {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] pathwayOrder;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] pathwayComponent;
    public ResourceRef organism;
}
