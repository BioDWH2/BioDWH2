package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"FoodID", "MeasureID", "ConversionFactorValue", "ConvFactorDateOfEntry"})
public class ConversionFactor {
    @JsonProperty("FoodID")
    public String foodID;
    @JsonProperty("MeasureID")
    public String measureID;
    @JsonProperty("ConversionFactorValue")
    public String conversionFactorValue;
    @JsonProperty("ConvFactorDateOfEntry")
    public String dateOfEntry;
}
