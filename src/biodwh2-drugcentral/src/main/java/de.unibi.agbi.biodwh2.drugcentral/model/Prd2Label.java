package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"ndcProductCode", "labelId", "id"})

public final class Prd2Label {
    @JsonProperty("ndcProductCode")
    public String ndcProductCode;
    @JsonProperty("labelId")
    public String labelId;
    @JsonProperty("id")
    public String id;
}
