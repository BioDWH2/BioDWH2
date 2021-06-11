package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

public class Nutrient {
    private final String id;
    private final String name;
    private final String nameF;
    private final String code;
    private final String symbol;
    private final String unit;
    private final String tagName;
    private final String decimals;

    public Nutrient(String id, String name, String nameF, String code, String symbol, String unit, String tagName,
                    String decimals) {
        this.id = id;
        this.name = name;
        this.nameF = nameF;
        this.code = code;
        this.symbol = symbol;
        this.unit = unit;
        this.tagName = tagName;
        this.decimals = decimals;
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

    public String getSymbol() {
        return symbol;
    }

    public String getUnit() {
        return unit;
    }

    public String getTagName() {
        return tagName;
    }

    public String getDecimals() {
        return decimals;
    }
}
