package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExperimentalPropertyKind {
    Water_Solubility("Water Solubility"),
    Melting_Point("Melting Point"),
    Boiling_Point("Boiling Point"),
    LogP("logP"),
    LogS("logS"),
    Hydrophobicity("Hydrophobicity"),
    Isoelectric_Point("Isoelectric Point"),
    Caco2_Permeability("caco2 Permeability"),
    PKa("pKa"),
    Molecular_Weight("Molecular Weight"),
    Molecular_Formula("Molecular Formula"),
    Radioactivity("Radioactivity");

    private ExperimentalPropertyKind(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
