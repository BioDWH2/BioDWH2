package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "name", "casRegNo", "inchi", "nostereoInchi", "molfile", "molimg",
"smiles", "inchiKey"})

public final class Parentmol {
    @JsonProperty("cdId")
    public String cdId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("casRegNo")
    public String casRegNo;
    @JsonProperty("inchi")
    public String inchi;
    @JsonProperty("nostereoInchi")
    public String nostereoInchi;
    @JsonProperty("molfile")
    public String molfile;
    @JsonProperty("molimg")
    public String molimg;
    @JsonProperty("smiles")
    public String smiles;
    @JsonProperty("inchiKey")
    public String inchiKey;
}
