package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "struct_id", "drug_class_id"})
public final class Struct2DrgClass {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("drug_class_id")
    public String drugClassId;
}
