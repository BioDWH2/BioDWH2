package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"id", "stem", "definition", "national_name", "length", "discontinued"})
@NodeLabels({"InnStem"})
public final class InnStem {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("stem")
    @GraphProperty("stem")
    public String stem;
    @JsonProperty("definition")
    @GraphProperty("definition")
    public String definition;
    @JsonProperty("national_name")
    @GraphProperty("national_name")
    public String nationalName;
    @JsonProperty("length")
    @GraphProperty("length")
    public String length;
    @JsonProperty("discontinued")
    @GraphProperty("discontinued")
    public String discontinued;
}
