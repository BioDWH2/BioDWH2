package de.unibi.agbi.biodwh2.medrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Property")
public final class Property {
    public String namespace;
    @GraphProperty("name")
    public String name;
    @GraphProperty("value")
    public String value;
}
