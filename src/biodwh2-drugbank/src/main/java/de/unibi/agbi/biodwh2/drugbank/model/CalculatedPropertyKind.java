package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CalculatedPropertyKind {
    LogP("logP"),
    LogS("logS"),
    Water_Solubility("Water Solubility"),
    IUPAC_Name("IUPAC Name"),
    Traditional_IUPAC_Name("Traditional IUPAC Name"),
    Molecular_Weight("Molecular Weight"),
    Monoisotopic_Weight("Monoisotopic Weight"),
    SMILES("SMILES"),
    Molecular_Formula("Molecular Formula"),
    InChI("InChI"),
    InChIKey("InChIKey"),
    Polar_Surface_Area("Polar Surface Area (PSA)"),
    Refractivity("Refractivity"),
    Polarizability("Polarizability"),
    Rotatable_Bond_Count("Rotatable Bond Count"),
    H_Bond_Acceptor_Count("H Bond Acceptor Count"),
    H_Bond_Donor_Count("H Bond Donor Count"),
    PKa_Strongest_Acidic("pKa (strongest acidic)"),
    PKa_Strongest_Basic("pKa (strongest basic)"),
    Physiological_Charge("Physiological Charge"),
    Number_Of_Rings("Number of Rings"),
    Bioavailability("Bioavailability"),
    Rule_Of_Five("Rule of Five"),
    Ghose_Filter("Ghose Filter"),
    MDDR_Like_Rule("MDDR-Like Rule"),
    Vebers_Rule("Veber's Rule");

    private CalculatedPropertyKind(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
