package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class PhysicalEntity extends Entity {
    public ResourceRef cellularLocation;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] feature;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] notFeature;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] memberPhysicalEntity;
}
