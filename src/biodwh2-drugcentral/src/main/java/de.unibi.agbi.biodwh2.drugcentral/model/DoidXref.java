package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "doid", "source", "xref"})

public final class DoidXref {
    @JsonProperty("id")
    public String id;
    @JsonProperty("doid")
    public String doid;
    @JsonProperty("source")
    public String source;
    @JsonProperty("xref")
    public String xref;
}
