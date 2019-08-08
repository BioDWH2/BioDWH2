package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Salt {
    @JsonProperty("drugbank-id")
    public DrugbankDrugSaltId drugbankId;
    public String name;
    public String unii;
    @JsonProperty("cas-number")
    public String casNumber;
    public String inchikey;
    @JsonProperty("average-mass")
    public float averageMass;
    @JsonProperty("monoisotopic-mass")
    public float monoisotopicMass;
}
