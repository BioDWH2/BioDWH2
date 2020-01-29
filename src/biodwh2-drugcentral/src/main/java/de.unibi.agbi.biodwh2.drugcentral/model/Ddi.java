package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "drug_class1", "drug_class2", "ddi_ref_id", "ddi_risk", "description", "source_id"})

public final class Ddi {
    @JsonProperty("id")
    public String id;
    @JsonProperty("drug_class1")
    public String drugClass1;
    @JsonProperty("drug_class2")
    public String drugClass2;
    @JsonProperty("ddi_ref_id")
    public String ddiRefId;
    @JsonProperty("ddi_risk")
    public String ddiRisk;
    @JsonProperty("description")
    public String description;
    @JsonProperty("source_id")
    public String sourceId;

}
