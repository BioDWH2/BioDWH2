package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

public class Yield {
    private final String id;
    private final String name;
    private final String nameF;

    public Yield(String id, String name, String nameF) {
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
