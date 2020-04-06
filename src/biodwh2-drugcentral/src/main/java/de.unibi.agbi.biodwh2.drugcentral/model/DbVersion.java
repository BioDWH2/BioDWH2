package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"version", "dtime"})
@NodeLabels({"DbVersion"})
public final class DbVersion {
    @JsonProperty("version")
    @GraphProperty("version")
    public String version;
    @JsonProperty("dtime")
    @GraphProperty("dtime")
    public String dtime;
}
