package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class DnaRegionReference extends EntityReference {
    public ResourceRef organism;
    @GraphProperty("sequence")
    public String sequence;
}
