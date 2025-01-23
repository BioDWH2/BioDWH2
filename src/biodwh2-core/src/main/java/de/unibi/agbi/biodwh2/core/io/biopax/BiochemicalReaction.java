package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class BiochemicalReaction extends Conversion {
    public ResourceRef eCNumber;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] left;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] right;
    public String conversionDirection;
}
