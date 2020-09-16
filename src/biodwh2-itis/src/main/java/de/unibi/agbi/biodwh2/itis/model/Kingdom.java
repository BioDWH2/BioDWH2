package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"kingdom_id", "kingdom_name", "update_date"})
public class Kingdom {
    @JsonProperty("kingdom_id")
    public int id;
    @JsonProperty("kingdom_name")
    public String name;
    @JsonProperty("update_date")
    public String updateDate;
}
