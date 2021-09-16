package de.unibi.agbi.biodwh2.drugbank.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("InternationalBrand")
public final class InternationalBrand {
    @GraphProperty("name")
    public String name;
    @GraphProperty("company")
    public String company;
}
