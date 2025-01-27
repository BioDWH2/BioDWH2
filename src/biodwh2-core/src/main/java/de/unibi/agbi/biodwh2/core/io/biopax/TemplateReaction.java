package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class TemplateReaction extends Interaction {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] product;
    @GraphProperty(value = "template_direction", transformation = ValueTransformation.ENUM_TO_STRING)
    public TemplateDirection templateDirection;
    public ResourceRef template;
}
