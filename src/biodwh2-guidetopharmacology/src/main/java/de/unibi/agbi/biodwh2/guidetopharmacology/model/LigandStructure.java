package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "ligand_id", "isomeric_smiles", "isomeric_standard_inchi", "isomeric_standard_inchi_key", "nonisomeric_smiles",
        "nonisomeric_standard_inchi", "nonisomeric_standard_inchi_key", "pubchem_cid"
})
public class LigandStructure {
    @JsonProperty("ligand_id")
    public Long ligandId;
    @JsonProperty("isomeric_smiles")
    @GraphProperty("isomeric_smiles")
    public String isomericSmiles;
    @JsonProperty("isomeric_standard_inchi")
    @GraphProperty("isomeric_standard_inchi")
    public String isomericStandardInchi;
    @JsonProperty("isomeric_standard_inchi_key")
    @GraphProperty("isomeric_standard_inchi_key")
    public String isomericStandardInchiKey;
    @JsonProperty("nonisomeric_smiles")
    @GraphProperty("nonisomeric_smiles")
    public String nonisomericSmiles;
    @JsonProperty("nonisomeric_standard_inchi")
    @GraphProperty("nonisomeric_standard_inchi")
    public String nonisomericStandardInchi;
    @JsonProperty("nonisomeric_standard_inchi_key")
    @GraphProperty("nonisomeric_standard_inchi_key")
    public String nonisomericStandardInchiKey;
    @JsonProperty("pubchem_cid")
    @GraphProperty("pubchem_cid")
    public Long pubchemCid;
}
