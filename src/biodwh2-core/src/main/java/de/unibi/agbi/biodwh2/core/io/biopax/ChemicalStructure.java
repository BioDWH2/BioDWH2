package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

public class ChemicalStructure extends UtilityClass {
    @GraphProperty(value = "structure_format", transformation = ValueTransformation.ENUM_TO_STRING)
    public StructureFormat structureFormat;
    @GraphProperty("structure_data")
    public String structureData;
}
