package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Catalysis extends Control {
    public CatalysisDirection catalysisDirection;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] cofactor;
}
