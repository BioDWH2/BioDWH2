package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "inn_number", "inn", "cas", "smiles", "smiles_salts_stripped", "inchi_key_salts_stripped",
        "nonisomeric_smiles_salts_stripped", "nonisomeric_inchi_key_salts_stripped", "neutralised_smiles",
        "neutralised_inchi_key", "neutralised_nonisomeric_smiles", "neutralised_nonisomeric_inchi_key", "inn_vector"
})
@GraphNodeLabel("INN")
public class INN {
    @JsonProperty("inn_number")
    @GraphProperty("id")
    public Long innNumber;
    @JsonProperty("inn")
    @GraphProperty("inn")
    public String inn;
    @JsonProperty("cas")
    @GraphProperty("cas")
    public String cas;
    @JsonProperty("smiles")
    @GraphProperty("smiles")
    public String smiles;
    @JsonProperty("smiles_salts_stripped")
    @GraphProperty("smiles_salts_stripped")
    public String smilesSaltsStripped;
    @JsonProperty("inchi_key_salts_stripped")
    @GraphProperty("inchi_key_salts_stripped")
    public String inchiKeySaltsStripped;
    @JsonProperty("nonisomeric_smiles_salts_stripped")
    @GraphProperty("nonisomeric_smiles_salts_stripped")
    public String nonisomericSmilesSaltsStripped;
    @JsonProperty("nonisomeric_inchi_key_salts_stripped")
    @GraphProperty("nonisomeric_inchi_key_salts_stripped")
    public String nonisomericInchiKeySaltsStripped;
    @JsonProperty("neutralised_smiles")
    @GraphProperty("neutralised_smiles")
    public String neutralisedSmiles;
    @JsonProperty("neutralised_inchi_key")
    @GraphProperty("neutralised_inchi_key")
    public String neutralisedInchiKey;
    @JsonProperty("neutralised_nonisomeric_smiles")
    @GraphProperty("neutralised_nonisomeric_smiles")
    public String neutralisedNonisomericSmiles;
    @JsonProperty("neutralised_nonisomeric_inchi_key")
    @GraphProperty("neutralised_nonisomeric_inchi_key")
    public String neutralisedNonisomericInchiKey;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("inn_vector")
    public String innVector;
}
