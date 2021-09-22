package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import java.time.LocalDateTime;

public class ConversionFactor {
    private final String foodID;
    private final String measureID;
    private final double conversionValue;
    private final LocalDateTime DateOfEntry;

    public ConversionFactor(String foodID, String measureID, double conversionValue, LocalDateTime dateOfEntry) {
        this.foodID = foodID;
        this.measureID = measureID;
        this.conversionValue = conversionValue;
        DateOfEntry = dateOfEntry;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getMeasureID() {
        return measureID;
    }

    public double getConversionValue() {
        return conversionValue;
    }

    public LocalDateTime getDateOfEntry() {
        return DateOfEntry;
    }
}
