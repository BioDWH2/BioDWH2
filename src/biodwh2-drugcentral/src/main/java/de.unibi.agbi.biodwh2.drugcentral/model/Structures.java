package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "cd_id", "cd_formular", "cd_molweight", "id", "clogp", "alogs", "cas_reg_no", "tpsa", "lipinski", "name",
        "no_formulations", "stem", "molfile", "mrdef", "enhanced_stereo", "arom_c", "sp3_c", "sp2_c", "spc", "halogen",
        "hetero_sp2_c", "rotb", "molimg", "o_n", "oh_nh", "inchi", "smiles", "rgb", "fda_labels", "inchikey"
})

public final class Structures {
    @JsonProperty("cd_id")
    public String cdId;
    @JsonProperty("cd_formular")
    public String cdFormular;
    @JsonProperty("cd_molweight")
    public String cdMolweight;
    @JsonProperty("id")
    public String id;
    @JsonProperty("clogp")
    public String clogp;
    @JsonProperty("alogs")
    public String alogs;
    @JsonProperty("cas_reg_no")
    public String casRegNo;
    @JsonProperty("tpsa")
    public String tpsa;
    @JsonProperty("lipinski")
    public String lipinski;
    @JsonProperty("name")
    public String name;
    @JsonProperty("no_formulations")
    public String noFormulations;
    @JsonProperty("stem")
    public String stem;
    @JsonProperty("molfile")
    public String molfile;
    @JsonProperty("mrdef")
    public String mrdef;
    @JsonProperty("enhanced_stereo")
    public String enhancedStereo;
    @JsonProperty("arom_c")
    public String aromC;
    @JsonProperty("sp3_c")
    public String sp3C;
    @JsonProperty("sp2_c")
    public String sp2C;
    @JsonProperty("sp_c")
    public String spC;
    @JsonProperty("halogen")
    public String halogen;
    @JsonProperty("hetero_sp2_c")
    public String heteroSp2C;
    @JsonProperty("rotb")
    public String rotb;
    @JsonProperty("molimg")
    public String molimg;
    @JsonProperty("o_n")
    public String oN;
    @JsonProperty("oh_nh")
    public String oh_nh;
    @JsonProperty("inchi")
    public String inchi;
    @JsonProperty("smiles")
    public String smiles;
    @JsonProperty("rgb")
    public String rgb;
    @JsonProperty("fda_labels")
    public String fdaLabels;
    @JsonProperty("inchikey")
    public String inchiKey;
}
