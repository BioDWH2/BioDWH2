package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"id", "category", "title", "effective_date", "assigned_entity", "pdf_url"})
@NodeLabels({"Label"})
public final class Label {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("category")
    @GraphProperty("category")
    public String category;
    @JsonProperty("title")
    @GraphProperty("title")
    public String title;
    @JsonProperty("effective_date")
    @GraphProperty("effective_date")
    public String effectiveDate;
    @JsonProperty("assigned_entity")
    @GraphProperty("assigned_entity")
    public String assignedEntity;
    @JsonProperty("pdf_url")
    @GraphProperty("pdf_url")
    public String pdfUrl;
}
