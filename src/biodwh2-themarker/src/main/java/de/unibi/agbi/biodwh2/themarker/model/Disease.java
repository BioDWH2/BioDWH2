package de.unibi.agbi.biodwh2.themarker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Disease ID", "Disease Name", "ICD-11", "Disease Class"})
public class Disease {
    @JsonProperty("Disease ID")
    public String id;
    @JsonProperty("Disease Name")
    public String name;
    @JsonProperty("ICD-11")
    public String icd11;
    @JsonProperty("Disease Class")
    public String _class;
}
