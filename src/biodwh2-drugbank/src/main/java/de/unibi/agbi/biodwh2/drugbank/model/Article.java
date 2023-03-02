package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@GraphNodeLabel("Article")
public final class Article {
    @JsonProperty("ref-id")
    @GraphProperty("id")
    public String refId;
    @JsonProperty("pubmed-id")
    @GraphProperty("pubmed_id")
    public Integer pubmedId;
    @GraphProperty("citation")
    public String citation;
}
