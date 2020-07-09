package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"code", "description"})
@NodeLabel("ObPatentUseCode")
public final class ObPatentUseCode {
    @JsonProperty("code")
    @GraphProperty("code")
    public String code;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
}
