package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class SmallMoleculeReference extends EntityReference {
    public ResourceRef structure;
    @GraphProperty("chemical_formula")
    public String chemicalFormula;
    /**
     * Datatype is officially float, but to prevent float representation errors, string is used.
     */
    @GraphProperty("molecular_weight")
    public String molecularWeight;
}
