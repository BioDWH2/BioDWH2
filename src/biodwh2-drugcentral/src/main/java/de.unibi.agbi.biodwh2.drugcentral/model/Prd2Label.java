package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"ndc_product_code", "label_id", "id"})

public final class Prd2Label {
    @JsonProperty("ndc_product_code")
    public String ndcProductCode;
    @JsonProperty("label_id")
    public String labelId;
    @JsonProperty("id")
    public String id;
}
