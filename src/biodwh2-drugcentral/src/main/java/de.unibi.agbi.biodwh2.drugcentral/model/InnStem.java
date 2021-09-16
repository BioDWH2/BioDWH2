package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "stem", "definition", "national_name", "length", "discontinued"})
@GraphNodeLabel("InnStem")
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
    public Integer length;
    @JsonProperty("discontinued")
    @GraphBooleanProperty(value = "discontinued", truthValue = "t")
    public String discontinued;
}
