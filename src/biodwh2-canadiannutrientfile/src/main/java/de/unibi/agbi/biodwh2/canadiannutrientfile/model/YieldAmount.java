package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import java.time.LocalDateTime;

public class YieldAmount {
    private final String foodID;
    private final String yieldID;
    private final double amount;
    private final LocalDateTime DateOfEntry;

    public YieldAmount(String foodID, String yieldID, double amount, LocalDateTime dateOfEntry) {
        this.foodID = foodID;
        this.yieldID = yieldID;
        this.amount = amount;
        DateOfEntry = dateOfEntry;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getYieldID() {
        return yieldID;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateOfEntry() {
        return DateOfEntry;
    }
}
