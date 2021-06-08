package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "property_type_id", "property_type_symbol", "struct_id", "value", "reference_id", "reference_type",
        "source"
})
@GraphNodeLabel("Property")
public class Property {
    @JsonProperty("id")
    public String id;
    @JsonProperty("property_type_id")
    public Integer propertyTypeId;
    @JsonProperty("property_type_symbol")
    @GraphProperty("type_symbol")
    public String propertyTypeSymbol;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("value")
    @GraphProperty("value")
    public String value;
    @JsonProperty("reference_id")
    public String referenceId;
    @JsonProperty("reference_type")
    public String referenceType;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
}
