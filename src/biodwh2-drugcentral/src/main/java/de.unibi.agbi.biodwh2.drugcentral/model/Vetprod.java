package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.drugcentral.etl.DrugCentralGraphExporter;

@JsonPropertyOrder({"prodid", "appl_type", "appl_no", "trade_name", "applicant", "active_ingredients_count"})
@GraphNodeLabel(DrugCentralGraphExporter.VET_PROD_LABEL)
public class Vetprod {
    @JsonProperty("prodid")
    @GraphProperty("id")
    public Long prodId;
    @JsonProperty("appl_type")
    @GraphProperty("appl_type")
    public String applType;
    @JsonProperty("appl_no")
    @GraphProperty("appl_no")
    public String applNo;
    @JsonProperty("trade_name")
    @GraphProperty("trade_name")
    public String tradeName;
    @JsonProperty("applicant")
    @GraphProperty("applicant")
    public String applicant;
    @JsonProperty("active_ingredients_count")
    @GraphProperty("active_ingredients_count")
    public Long activeIngredientsCount;
}
