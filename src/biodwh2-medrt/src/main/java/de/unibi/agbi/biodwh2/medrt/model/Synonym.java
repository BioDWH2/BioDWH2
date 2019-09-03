package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Synonym {
    public String namespace;
    public String name;
    @JsonProperty("to_namespace")
    public String toNamespace;
    @JsonProperty("to_name")
    public String toName;
    public boolean preferred;
}
