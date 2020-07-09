package de.unibi.agbi.biodwh2.medrt.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("Term")
public class Term {
    @GraphProperty("name")
    public String name;
    public String namespace;
    @GraphProperty("status")
    public String status;
}
