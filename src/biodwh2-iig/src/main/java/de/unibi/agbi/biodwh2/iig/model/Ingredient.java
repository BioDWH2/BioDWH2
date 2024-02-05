package de.unibi.agbi.biodwh2.iig.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.iig.etl.IIGGraphExporter;

@JsonPropertyOrder({
        "INGREDIENT_NAME", "ROUTE", "DOSAGE_FORM", "CAS_NUMBER", "UNII", "POTENCY_AMOUNT", "POTENCY_UNIT",
        "MAXIMUM_DAILY_EXPOSURE", "MAXIMUM_DAILY_EXPOSURE_UNIT", "RECORD_UPDATED"
})
@GraphNodeLabel(IIGGraphExporter.INGREDIENT_LABEL)
public class Ingredient {
    @JsonProperty("INGREDIENT_NAME")
    @GraphProperty("name")
    public String ingredientName;
    @JsonProperty("ROUTE")
    @GraphProperty("route")
    public String route;
    @JsonProperty("DOSAGE_FORM")
    @GraphProperty("dosage_form")
    public String dosageForm;
    @JsonProperty("CAS_NUMBER")
    @GraphProperty("cas_number")
    public String casNumber;
    @JsonProperty("UNII")
    @GraphProperty(value = "unii", emptyPlaceholder = "NA")
    public String unii;
    @JsonProperty("POTENCY_AMOUNT")
    @GraphProperty("potency_amount")
    public String potencyAmount;
    @JsonProperty("POTENCY_UNIT")
    @GraphProperty("potency_unit")
    public String potencyUnit;
    @JsonProperty("MAXIMUM_DAILY_EXPOSURE")
    @GraphProperty("maximum_daily_exposure")
    public String maximumDailyExposure;
    @JsonProperty("MAXIMUM_DAILY_EXPOSURE_UNIT")
    @GraphProperty("maximum_daily_exposure_unit")
    public String maximumDailyExposureUnit;
    @JsonProperty("RECORD_UPDATED")
    public String recordUpdated;
}
