package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "doid", "source", "xref"})
@GraphNodeLabel("DOTermXref")
public final class DoidXref {
    @JsonProperty("id")
    public String id;
    @JsonProperty("doid")
    public String doid;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
    @JsonProperty("xref")
    @GraphProperty("xref")
    public String xref;
}
