package de.unibi.agbi.biodwh2.core.model;

public enum IdentifierType {
    Dummy("Dummy"),
    HGNCSymbol("HGNC_Symbol"),
    HGNCId("HGNC"),
    DrugBank("DrugBank"),
    UNII("UNII"),
    CAS("CAS"),
    EuropeanChemicalsAgencyEC("ECA_EC"),
    RxNormCUI("RxNorm_CUI"),
    PubChemCompound("PubChem_CID"),
    PubChemSubstance("PubChem_SID"),
    UMLSCui("UMLS_CUI");

    IdentifierType(final String prefix) {
        this.prefix = prefix;
    }

    public final String prefix;
}
