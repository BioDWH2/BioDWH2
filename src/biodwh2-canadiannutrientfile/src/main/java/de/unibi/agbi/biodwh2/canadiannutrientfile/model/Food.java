package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import java.time.LocalDateTime;

public class Food {
    private final String id;
    private final String code;
    private final String foodGroupID;
    private final String foodSourceID;
    private final String description;
    private final String descriptionF;
    private final int countryCode;
    private final LocalDateTime DateOfEntry;
    private final LocalDateTime DateOfPublication;
    private final String scientificName;

    public Food(String id, String code, String foodGroupID, String foodSourceID, String description,
                String descriptionF, int countryCode, LocalDateTime dateOfEntry, LocalDateTime dateOfPublication,
                String scientificName) {
        this.id = id;
        this.code = code;
        this.foodGroupID = foodGroupID;
        this.foodSourceID = foodSourceID;
        this.description = description;
        this.descriptionF = descriptionF;
        this.countryCode = countryCode;
        DateOfEntry = dateOfEntry;
        DateOfPublication = dateOfPublication;
        this.scientificName = scientificName;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFoodGroupID() {
        return foodGroupID;
    }

    public String getFoodSourceID() {
        return foodSourceID;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionF() {
        return descriptionF;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public LocalDateTime getDateOfEntry() {
        return DateOfEntry;
    }

    public LocalDateTime getDateOfPublication() {
        return DateOfPublication;
    }

    public String getScientificName() {
        return scientificName;
    }
}
