package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExternalIdentifierResource {
    UniProtKB("UniProtKB"),
    Wikipedia("Wikipedia"),
    ChEBI("ChEBI"),
    ChEMBL("ChEMBL"),
    PubChem_Compound("PubChem Compound"),
    PubChem_Substance("PubChem Substance"),
    Drugs_Product_Database("Drugs Product Database (DPD)"),
    KEGG_Compound("KEGG Compound"),
    KEGG_Drug("KEGG Drug"),
    ChemSpider("ChemSpider"),
    BindingDB("BindingDB"),
    National_Drug_Code_Directory("National Drug Code Directory"),
    GenBank("GenBank"),
    Therapeutic_Targets_Database("Therapeutic Targets Database"),
    PharmGKB("PharmGKB"),
    PDB("PDB"),
    IUPHAR("IUPHAR"),
    RxCUI("RxCUI"),
    ZINC("ZINC"),
    Guide_To_Pharmacology("Guide to Pharmacology");

    ExternalIdentifierResource(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
