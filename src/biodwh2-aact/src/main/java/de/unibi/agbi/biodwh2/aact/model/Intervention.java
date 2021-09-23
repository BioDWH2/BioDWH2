package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "intervention_type", "name", "description"})
public class Intervention {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("intervention_type")
    public String interventionType;
    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;
}
