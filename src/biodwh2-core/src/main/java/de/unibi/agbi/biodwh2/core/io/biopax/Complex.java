package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Complex extends PhysicalEntity {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] component;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] componentStoichiometry;
}
