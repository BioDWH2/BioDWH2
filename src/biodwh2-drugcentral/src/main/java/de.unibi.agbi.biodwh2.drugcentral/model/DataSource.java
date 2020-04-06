package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"src_id", "source_name"})
@NodeLabels({"DataSource"})
public final class DataSource {
    @JsonProperty("src_id")
    @GraphProperty("src_id")
    public String srcId;
    @JsonProperty("source_name")
    @GraphProperty("source_name")
    public String sourceName;
}
