package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Drug ID", "Clinical Status", "Disease", "ICD Code"})
public class DrugClinical {
    @JsonProperty("Drug ID")
    public String drugId;
    @JsonProperty("Clinical Status")
    public String clinicalStatus;
    @JsonProperty("Disease")
    public String disease;
    @JsonProperty("ICD Code")
    public String icdCode;
}
