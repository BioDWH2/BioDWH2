package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsGraphExporter;

@GraphNodeLabel(OpenTargetsGraphExporter.PATHWAY_LABEL)
public class Reactome {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("label")
    @GraphProperty("label")
    public String label;
    @JsonProperty("ancestors")
    public String[] ancestors;
    @JsonProperty("descendants")
    public String[] descendants;
    @JsonProperty("children")
    public String[] children;
    @JsonProperty("parents")
    public String[] parents;
    @JsonProperty("path")
    public String[][] path;
}
