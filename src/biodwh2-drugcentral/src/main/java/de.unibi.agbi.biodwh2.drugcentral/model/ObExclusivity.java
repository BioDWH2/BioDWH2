package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "appl_type", "appl_no", "product_no", "exclusivity_code", "exclusivity_date"})
@NodeLabel("OrangeBookExclusivity")
public final class ObExclusivity {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("appl_type")
    @GraphProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    @GraphProperty("appl_no")
    public String applNo;
    @JsonProperty("product_no")
    @GraphProperty("product_no")
    public String productNo;
    @JsonProperty("exclusivity_code")
    public String exclusivityCode;
    @JsonProperty("exclusivity_date")
    @GraphProperty("exclusivity_date")
    public String exclusivityDate;
}
