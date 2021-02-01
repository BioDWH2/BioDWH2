package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Textbook"})
public final class Textbook {
    @JsonProperty("ref-id")
    @GraphProperty("id")
    public String refId;
    @GraphProperty("isbn")
    public String isbn;
    @GraphProperty("citation")
    public String citation;
}
