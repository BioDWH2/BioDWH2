package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NumericDataset extends Dataset {
    @JsonProperty("value")
    public String value;
    @JsonProperty("num_value")
    public Double numValue;
    @JsonProperty("min_value")
    public Double minValue;
    @JsonProperty("max_value")
    public Double maxValue;
}
