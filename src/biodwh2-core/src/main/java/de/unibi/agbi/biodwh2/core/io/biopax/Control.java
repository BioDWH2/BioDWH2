package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class Control extends Interaction {
    public ResourceRef controlled;
    public ResourceRef controller;
    @GraphProperty(value = "control_type", transformation = ValueTransformation.ENUM_TO_STRING)
    public ControlType controlType;
}
