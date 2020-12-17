package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "cd_id", "cd_formula", "cd_molweight", "id", "clogp", "alogs", "cas_reg_no", "tpsa", "lipinski", "name",
        "no_formulations", "stem", "molfile", "mrdef", "enhanced_stereo", "arom_c", "sp3_c", "sp2_c", "sp_c", "halogen",
        "hetero_sp2_c", "rotb", "molimg", "o_n", "oh_nh", "inchi", "smiles", "rgb", "fda_labels", "inchikey", "status"
})
@NodeLabel("Structure")
public final class Structure {
    @JsonProperty("cd_id")
    @GraphProperty("cd_id")
    public String cdId;
    @JsonProperty("cd_formula")
    @GraphProperty("cd_formula")
    public String cdFormula;
    @JsonProperty("cd_molweight")
    @GraphProperty("cd_molweight")
    public String cdMolweight;
    @JsonProperty("id")
    @GraphProperty("id")
    public int id;
    @JsonProperty("clogp")
    @GraphProperty("clogp")
    public String clogp;
    @JsonProperty("alogs")
    @GraphProperty("alogs")
    public String alogs;
    @JsonProperty("cas_reg_no")
    @GraphProperty("cas_reg_no")
    public String casRegNo;
    @JsonProperty("tpsa")
    @GraphProperty("tpsa")
    public String tpsa;
    @JsonProperty("lipinski")
    @GraphProperty("lipinski")
    public Integer lipinski;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("no_formulations")
    @GraphProperty("no_formulations")
    public Integer noFormulations;
    @JsonProperty("stem")
    public String stem;
    @JsonProperty("molfile")
    @GraphProperty("molfile")
    public String molfile;
    @JsonProperty("mrdef")
    @GraphProperty("mrdef")
    public String mrdef;
    @JsonProperty("enhanced_stereo")
    @GraphProperty("enhanced_stereo")
    public String enhancedStereo;
    @JsonProperty("arom_c")
    @GraphProperty("arom_c")
    public Integer aromC;
    @JsonProperty("sp3_c")
    @GraphProperty("sp3_c")
    public Integer sp3C;
    @JsonProperty("sp2_c")
    @GraphProperty("sp2_c")
    public Integer sp2C;
    @JsonProperty("sp_c")
    @GraphProperty("sp_c")
    public Integer spC;
    @JsonProperty("halogen")
    @GraphProperty("halogen")
    public Integer halogen;
    @JsonProperty("hetero_sp2_c")
    @GraphProperty("hetero_sp2_c")
    public Integer heteroSp2C;
    @JsonProperty("rotb")
    @GraphProperty("rotb")
    public Integer rotb;
    @JsonProperty("molimg")
    public String molimg;
    @JsonProperty("o_n")
    @GraphProperty("o_n")
    public Integer oN;
    @JsonProperty("oh_nh")
    @GraphProperty("oh_nh")
    public Integer oh_nh;
    @JsonProperty("inchi")
    @GraphProperty("inchi")
    public String inchi;
    @JsonProperty("smiles")
    @GraphProperty("smiles")
    public String smiles;
    @JsonProperty("rgb")
    @GraphProperty("rgb")
    public Integer rgb;
    @JsonProperty("fda_labels")
    @GraphProperty("fda_labels")
    public Integer fdaLabels;
    @JsonProperty("inchikey")
    @GraphProperty("inchikey")
    public String inchiKey;
    @JsonProperty("status")
    @GraphProperty("status")
    public String status;
}
