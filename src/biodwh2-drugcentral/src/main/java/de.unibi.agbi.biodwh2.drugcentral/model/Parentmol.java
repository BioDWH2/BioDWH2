package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "cdid", "name", "cas_reg_no", "inchi", "nostereo_inchi", "molfile", "molimg", "smiles", "inchi_key"
})

public final class Parentmol {
    @JsonProperty("cdId")
    public String cdId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("cas_reg_no")
    public String casRegNo;
    @JsonProperty("inchi")
    public String inchi;
    @JsonProperty("nostereo_inchi")
    public String nostereoInchi;
    @JsonProperty("molfile")
    public String molfile;
    @JsonProperty("molimg")
    public String molimg;
    @JsonProperty("smiles")
    public String smiles;
    @JsonProperty("inchi_key")
    public String inchiKey;
}
