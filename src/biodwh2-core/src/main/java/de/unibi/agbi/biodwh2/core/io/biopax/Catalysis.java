package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class Catalysis extends Control {
    @GraphProperty(value = "catalysis_direction", transformation = ValueTransformation.ENUM_TO_STRING)
    public CatalysisDirection catalysisDirection;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ResourceRef[] cofactor;
}
