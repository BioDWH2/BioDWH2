package de.unibi.agbi.biodwh2.ttd.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("Drug")
public class Drug {
    @GraphProperty("identifier")
    public String identifier;
    @GraphProperty("trade_name")
    public String tradeName;
    @GraphProperty("company")
    public String company;
    @GraphProperty("name")
    public String name;
    @GraphProperty("inchi")
    public String inchi;
    @GraphProperty("type")
    public String type;
    @GraphProperty("synonyms")
    public String[] synonyms;
    @GraphProperty("inchi_key")
    public String inchikey;
    @GraphProperty("canonical_smiles")
    public String canonicalSmiles;
    @GraphProperty("highest_status")
    public String highestStatus;
    @GraphProperty("adi_id")
    public String adiID;
    @GraphProperty("cas_number")
    public String casNumber;
    @GraphProperty("formular")
    public String formular;
    @GraphProperty("pubchem_cids")
    public String[] pubChemCID;
    @GraphProperty("pubchem_sids")
    public String[] pubChemSID;
    @GraphProperty("chebi_id")
    public String chEBIid;
    @GraphProperty("superdrug_atcs")
    public String[] superDrugATC;
    @GraphProperty("superdrug_cas")
    public String superDrugCas;
    @GraphProperty("therapeutic_class")
    public String therapeuticClass;
    @GraphProperty("drug_class")
    public String drugClass;
    @GraphProperty("compound_class")
    public String compoundClass;
}
