package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Link")
public final class Link {
    @JsonProperty("ref-id")
    @GraphProperty("id")
    public String refId;
    @GraphProperty("title")
    public String title;
    @GraphProperty("url")
    public String url;
}
