package de.unibi.agbi.biodwh2.medrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Namespace"})
public class Namespace {
    @GraphProperty("name")
    public String name;
    @GraphProperty("code")
    public String code;
    @GraphProperty("version")
    public String version;
    @GraphProperty("authority")
    public String authority;
}
