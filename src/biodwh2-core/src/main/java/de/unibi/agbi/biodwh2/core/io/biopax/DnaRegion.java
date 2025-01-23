package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class DnaRegion extends PhysicalEntity {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] entityReference;
}
