package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class SequenceSite extends SequenceLocation {
    @GraphProperty("sequence_position")
    public Integer sequencePosition;
    @GraphProperty(value = "position_status", transformation = ValueTransformation.ENUM_TO_STRING)
    public PositionStatus positionStatus;
}
