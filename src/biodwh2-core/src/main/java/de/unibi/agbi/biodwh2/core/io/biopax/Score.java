package de.unibi.agbi.biodwh2.core.io.biopax;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class Score extends UtilityClass {
    public ResourceRef scoreSource;
    @GraphProperty("value")
    public String value;
}
