package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"variant_id", "reference_id"})
public class VariantRef {
    @JsonProperty("variant_id")
    public Long variantId;
    @JsonProperty("reference_id")
    public Long referenceId;
}
