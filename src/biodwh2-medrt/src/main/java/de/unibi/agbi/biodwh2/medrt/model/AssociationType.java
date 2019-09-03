package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssociationType {
    public String type;
    public String name;
    public String namespace;
    @JsonProperty("inverse_name")
    public String inverseName;
}
