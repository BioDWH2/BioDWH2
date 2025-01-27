package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class DeltaG extends UtilityClass {
    /**
     * Datatype is officially float, but to prevent float representation errors, string is used.
     */
    @GraphProperty("deltaG_prime0")
    public String deltaGPrime0;
    @GraphProperty("ionic_strength")
    public String ionicStrength;
    @GraphProperty("pH")
    public String pH;
    @GraphProperty("pMg")
    public String pMg;
    @GraphProperty("temperature")
    public String temperature;
}
