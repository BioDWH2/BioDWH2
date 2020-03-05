package de.unibi.agbi.biodwh2.medrt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Association"})
public class Association {
    public String namespace;
    @GraphProperty("name")
    public String name;
    @JsonProperty("from_namespace")
    @GraphProperty("from_namespace")
    public String fromNamespace;
    @JsonProperty("from_name")
    @GraphProperty("from_name")
    public String fromName;
    @JsonProperty("from_code")
    @GraphProperty("from_code")
    public String fromCode;
    @JsonProperty("to_namespace")
    @GraphProperty("to_namespace")
    public String toNamespace;
    @JsonProperty("to_name")
    @GraphProperty("to_name")
    public String toName;
    @JsonProperty("to_code")
    @GraphProperty("to_code")
    public String toCode;
    public Qualifier qualifier;
}
