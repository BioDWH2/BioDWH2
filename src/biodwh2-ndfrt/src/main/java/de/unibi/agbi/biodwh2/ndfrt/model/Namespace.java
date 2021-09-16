package de.unibi.agbi.biodwh2.ndfrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Namespace")
public final class Namespace {
    @GraphProperty("name")
    public String name;
    @GraphProperty("code")
    public String code;
    @GraphProperty("id")
    public String id;
}
