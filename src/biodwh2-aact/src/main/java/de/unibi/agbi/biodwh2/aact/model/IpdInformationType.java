package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "name"})
public class IpdInformationType {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("name")
    public String name;
}
