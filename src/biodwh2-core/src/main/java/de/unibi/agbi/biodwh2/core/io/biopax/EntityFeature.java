package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class EntityFeature extends UtilityClass {
    public ResourceRef featureLocationType;
    public ResourceRef featureLocation;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] evidence;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] memberFeature;
}
