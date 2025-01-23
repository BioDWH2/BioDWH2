package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class TemplateReaction extends Interaction {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] product;
    public TemplateDirection templateDirection;
    public ResourceRef template;
}
