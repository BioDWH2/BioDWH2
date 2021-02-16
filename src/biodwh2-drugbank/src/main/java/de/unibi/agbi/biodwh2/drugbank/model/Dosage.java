package de.unibi.agbi.biodwh2.drugbank.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Dosage"})
public final class Dosage {
    @GraphProperty("form")
    public String form;
    @GraphProperty("route")
    public String route;
    @GraphProperty("strength")
    public String strength;
}
