package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"cdId", "cdFormular", "cdMolweight", "id", "clogp", "alogs", "casRegNo", "tpsa",
"lipinski", "name", "noFormulations", "stem", "molfile", "mrdef", "enhancedStereo", "aromC", "sp3C", "sp2C",
"spc", "halogen", "heteroSp2C", "rotb", "molimg", "oN", "inchi", "smiles", "rgb", "fdaLabels", "inchiKey"})

public final class Structures {
    @JsonProperty("cdId")
    public String cdId;
    @JsonProperty("cdFormular")
    public String cdFormular;
    @JsonProperty("cdMolweight")
    public String cdMolweight;
    @JsonProperty("id")
    public String id;
    @JsonProperty("clogp")
    public String clogp;
    @JsonProperty("alogs")
    public String alogs;
    @JsonProperty("casRegNo")
    public String casRegNo;
    @JsonProperty("tpsa")
    public String tpsa;
    @JsonProperty("lipinski")
    public String lipinski;
    @JsonProperty("name")
    public String name;
    @JsonProperty("noFormulations")
    public String noFormulations;
    @JsonProperty("stem")
    public String stem;
    @JsonProperty("molfile")
    public String molfile;
    @JsonProperty("mrdef")
    public String mrdef;
    @JsonProperty("enhancedStereo")
    public String enhancedStereo;
    @JsonProperty("aromC")
    public String aromC;
    @JsonProperty("sp3C")
    public String sp3C;
    @JsonProperty("sp2C")
    public String sp2C;
    @JsonProperty("spC")
    public String spC;
    @JsonProperty("halogen")
    public String halogen;
    @JsonProperty("heteroSp2C")
    public String heteroSp2C;
    @JsonProperty("rotb")
    public String rotb;
    @JsonProperty("molimg")
    public String molimg;
    @JsonProperty("oN")
    public String ohNh;
    @JsonProperty("inchi")
    public String inchi;
    @JsonProperty("smiles")
    public String smiles;
    @JsonProperty("rgb")
    public String rgb;
    @JsonProperty("fdaLabels")
    public String fdaLabels;
    @JsonProperty("inchiKey")
    public String inchiKey;
}
