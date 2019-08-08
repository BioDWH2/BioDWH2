package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Article {
    @JsonProperty("ref-id")
    public String refId;
    @JsonProperty("pubmed-id")
    public String pubmedId;
    public String citation;
}
