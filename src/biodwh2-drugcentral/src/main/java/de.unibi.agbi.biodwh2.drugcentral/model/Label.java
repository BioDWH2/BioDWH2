package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "category", "title", "effectiveDate", "assignedEntity", "pdfUrl"})

public final class Label {
    @JsonProperty("id")
    public String id;
    @JsonProperty("category")
    public String category;
    @JsonProperty("title")
    public String title;
    @JsonProperty("effectiveDate")
    public String effectiveDate;
    @JsonProperty("assignedEntity")
    public String assignedEntity;
    @JsonProperty("pdfUrl")
    public String pdfUrl;
}
