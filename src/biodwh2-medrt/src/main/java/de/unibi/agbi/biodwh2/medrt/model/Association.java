package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Association {
    public String namespace;
    public String name;
    @JsonProperty("from_namespace")
    public String fromNamespace;
    @JsonProperty("from_name")
    public String fromName;
    @JsonProperty("from_code")
    public String fromCode;
    @JsonProperty("to_namespace")
    public String toNamespace;
    @JsonProperty("to_name")
    public String toName;
    @JsonProperty("to_code")
    public String toCode;
    public Qualifier qualifier;
}
