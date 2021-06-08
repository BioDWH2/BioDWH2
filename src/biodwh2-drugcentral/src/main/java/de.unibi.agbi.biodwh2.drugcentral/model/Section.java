package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "text", "label_id", "code", "title"})
@GraphNodeLabel("DrugLabelSection")
public final class Section {
    @JsonProperty("id")
    public String id;
    @JsonProperty("text")
    @GraphProperty("text")
    public String text;
    @JsonProperty("label_id")
    public String labelId;
    @JsonProperty("code")
    @GraphProperty("code")
    public String code;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
}
