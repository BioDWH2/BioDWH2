package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

public class FoodGroup {
    private final String id;
    private final String name;
    private final String nameF;
    private final String code;

    public FoodGroup(String id, String name, String nameF, String code) {
        this.id = id;
        this.name = name;
        this.nameF = nameF;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameF() {
        return nameF;
    }

    public String getCode() {
        return code;
    }
}
