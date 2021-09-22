package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"FoodID","YieldID","YieldAmount","YieldDateofEntry"})
public class YieldAmount {
    @JsonProperty("FoodID")
    public String foodID;
    @JsonProperty("YieldID")
    public String yieldID;
    @JsonProperty("YieldAmount")
    public String amount;
    @JsonProperty("YieldDateofEntry")
    public String dateOfEntry;
}
