package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class Interaction extends Entity {
    public ResourceRef interactionType;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] participant;
}
