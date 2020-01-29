package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "category", "title", "effective_date", "assigned_entity", "pdf_url"})

public final class Label {
    @JsonProperty("id")
    public String id;
    @JsonProperty("category")
    public String category;
    @JsonProperty("title")
    public String title;
    @JsonProperty("effective_date")
    public String effectiveDate;
    @JsonProperty("assigned_entity")
    public String assignedEntity;
    @JsonProperty("pdf_url")
    public String pdfUrl;
}
