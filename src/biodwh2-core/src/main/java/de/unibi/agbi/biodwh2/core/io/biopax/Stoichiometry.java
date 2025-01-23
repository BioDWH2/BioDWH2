package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class Stoichiometry extends UtilityClass {
    public ResourceRef physicalEntity;
    /**
     * Datatype is officially float, but to prevent float representation errors, string is used.
     */
    @GraphProperty("stoichiometric_coefficient")
    public String stoichiometricCoefficient;
}
