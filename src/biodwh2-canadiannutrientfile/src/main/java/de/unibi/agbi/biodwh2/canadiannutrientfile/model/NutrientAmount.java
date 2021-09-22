package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import java.time.LocalDateTime;

public class NutrientAmount {
    private final String foodID;
    private final String nutrientID;
    private final String nutrientSourceID;
    private final double value;
    private final double standardError;
    private final int numberOfObservations;
    private final LocalDateTime DateOfEntry;

    public NutrientAmount(String foodID, String nutrientID, String nutrientSourceID, double value, double standardError,
                          int numberOfObservations, LocalDateTime dateOfEntry) {
        this.foodID = foodID;
        this.nutrientID = nutrientID;
        this.nutrientSourceID = nutrientSourceID;
        this.value = value;
        this.standardError = standardError;
        this.numberOfObservations = numberOfObservations;
        DateOfEntry = dateOfEntry;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getNutrientID() {
        return nutrientID;
    }

    public String getNutrientSourceID() {
        return nutrientSourceID;
    }

    public double getValue() {
        return value;
    }

    public double getStandardError() {
        return standardError;
    }

    public int getNumberOfObservations() {
        return numberOfObservations;
    }

    public LocalDateTime getDateOfEntry() {
        return DateOfEntry;
    }
}
