package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "text", "labelId", "code", "title"})

public final class Section {
    @JsonProperty("id")
    public String id;
    @JsonProperty("text")
    public String text;
    @JsonProperty("labelId")
    public String labelId;
    @JsonProperty("code")
    public String code;
    @JsonProperty("title")
    public String title;
}
