package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"FoodID", "RefuseID", "RefuseAmount", "RefuseDateOfEntry"})
public class RefuseAmount {
    @JsonProperty("FoodID")
    public String foodID;
    @JsonProperty("RefuseID")
    public String refuseID;
    @JsonProperty("RefuseAmount")
    public String amount;
    @JsonProperty("RefuseDateOfEntry")
    public String dateOfEntry;
}
