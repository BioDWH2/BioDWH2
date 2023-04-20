package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("PathwayReaction")
@JsonPropertyOrder({
        "From", "To", "Reaction Type", "Controller", "Control Type", "Cell Type", "PMIDs", "Genes", "Drugs", "Diseases",
        "Summary"
})
public class Pathway {
    @JsonProperty("From")
    @GraphProperty("from")
    public String from;
    @JsonProperty("To")
    @GraphProperty("to")
    public String to;
    @JsonProperty("Reaction Type")
    @GraphProperty("reaction_type")
    public String reactionType;
    @JsonProperty("Controller")
    @GraphProperty("controller")
    public String controller;
    @JsonProperty("Control Type")
    @GraphProperty("control_type")
    public String controlType;
    @JsonProperty("Cell Type")
    @GraphProperty("cell_type")
    public String cellType;
    @JsonProperty("PMIDs")
    @GraphArrayProperty(value = "pmids", arrayDelimiter = ", ")
    public String pmids;
    @JsonProperty("Genes")
    @GraphProperty("genes")
    public String genes;
    @JsonProperty("Drugs")
    @GraphProperty("drugs")
    public String drugs;
    @JsonProperty("Diseases")
    @GraphProperty("diseases")
    public String diseases;
    @JsonProperty("Summary")
    @GraphProperty("summary")
    public String summary;
}
