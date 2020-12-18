package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "active_moiety_unii", "active_moiety_name", "unit", "quantity", "substance_unii", "substance_name",
        "ndc_product_code", "struct_id", "quantity_denom_unit", "quantity_denom_value"
})
@NodeLabels("ActiveIngredient")
public final class ActiveIngredient {
    @JsonProperty("id")
    public String id;
    @JsonProperty("active_moiety_unii")
    @GraphProperty("active_moiety_unii")
    public String activeMoietyUnii;
    @JsonProperty("active_moiety_name")
    @GraphProperty("active_moiety_name")
    public String activeMoietyName;
    @JsonProperty("unit")
    @GraphProperty("unit")
    public String unit;
    @JsonProperty("quantity")
    @GraphProperty("quantity")
    public String quantity;
    @JsonProperty("substance_unii")
    @GraphProperty("substance_unii")
    public String substanceUnii;
    @JsonProperty("substance_name")
    @GraphProperty("substance_name")
    public String substanceName;
    @JsonProperty("ndc_product_code")
    public String ndcProductCode;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("quantity_denom_unit")
    @GraphProperty("quantity_denom_unit")
    public String quantityDenomUnit;
    @JsonProperty("quantity_denom_value")
    @GraphProperty("quantity_denom_value")
    public String quantityDenomValue;
}
