package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

public class Refuse {
    private final String id;
    private final String name;
    private final String nameF;

    public Refuse(String id, String name, String nameF) {
        this.id = id;
        this.name = name;
        this.nameF = nameF;
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
}
