package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import java.time.LocalDateTime;

public class RefuseAmount {
    private final String foodID;
    private final String refuseID;
    private final double amount;
    private final LocalDateTime DateOfEntry;

    public RefuseAmount(String foodID, String refuseID, double amount, LocalDateTime dateOfEntry) {
        this.foodID = foodID;
        this.refuseID = refuseID;
        this.amount = amount;
        DateOfEntry = dateOfEntry;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getRefuseID() {
        return refuseID;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateOfEntry() {
        return DateOfEntry;
    }
}
