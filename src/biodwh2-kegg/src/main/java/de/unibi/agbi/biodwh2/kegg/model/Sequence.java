package de.unibi.agbi.biodwh2.kegg.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Sequence")
public class Sequence {
    @GraphProperty("sequence")
    public String sequence;
    @GraphProperty("type")
    public String type;
}
