package de.unibi.agbi.biodwh2.medrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@NodeLabels("PropertyDefinition")
public final class PropertyType {
    @GraphProperty("type")
    public String type;
    @GraphProperty("name")
    public String name;
    public String namespace;
}
