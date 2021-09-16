package de.unibi.agbi.biodwh2.medrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@GraphNodeLabel("Term")
public final class Term {
    @GraphProperty("name")
    public String name;
    @GraphProperty("namespace")
    public String namespace;
    @GraphProperty("status")
    public String status;
}
