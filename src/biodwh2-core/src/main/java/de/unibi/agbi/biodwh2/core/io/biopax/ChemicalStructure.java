package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class ChemicalStructure extends UtilityClass {
    public StructureFormat structureFormat;
    @GraphProperty("structure_data")
    public String structureData;
}
