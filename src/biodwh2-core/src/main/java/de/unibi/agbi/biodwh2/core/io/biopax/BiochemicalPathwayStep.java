package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class BiochemicalPathwayStep extends PathwayStep {
    public ResourceRef stepConversion;
    @GraphProperty(value = "step_direction", transformation = ValueTransformation.ENUM_TO_STRING)
    public StepDirection stepDirection;
}
