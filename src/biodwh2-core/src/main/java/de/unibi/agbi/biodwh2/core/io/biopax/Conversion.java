package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class Conversion extends Interaction {
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] left;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] right;
    @GraphProperty(value = "conversion_direction", transformation = ValueTransformation.ENUM_TO_STRING)
    public ConversionDirection conversionDirection;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] participantStoichiometry;
    @GraphProperty("spontaneous")
    public Boolean spontaneous;
}
