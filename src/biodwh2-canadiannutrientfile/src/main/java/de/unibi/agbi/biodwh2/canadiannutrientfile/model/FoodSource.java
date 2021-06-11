package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

public class FoodSource {
    private final String id;
    private final String code;
    private final String description;
    private final String descriptionF;

    public FoodSource(String id, String code, String description, String descriptionF) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.descriptionF = descriptionF;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionF() {
        return descriptionF;
    }
}
