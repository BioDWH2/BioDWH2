package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PolypeptideExternalIdentifierResource {
    UniProtKB("UniProtKB"),
    UniProt_Accession("UniProt Accession"),
    HUGO_Gene_Nomenclature_Committee("HUGO Gene Nomenclature Committee (HGNC)"),
    Human_Protein_Reference_Database("Human Protein Reference Database (HPRD)"),
    GenAtlas("GenAtlas"),
    GeneCards("GeneCards"),
    GenBank_Gene_Database("GenBank Gene Database"),
    GenBank_Protein_Database("GenBank Protein Database"),
    ChEMBL("ChEMBL"),
    IUPHAR("IUPHAR"),
    Guide_To_Pharmacology("Guide to Pharmacology");

    private PolypeptideExternalIdentifierResource(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
