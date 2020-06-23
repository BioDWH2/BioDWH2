package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "cd_id", "name", "cas_reg_no", "inchi", "nostereo_inchi", "molfile", "molimg", "smiles", "inchikey"
})
@NodeLabels({"Parentmol"})
public final class Parentmol {
    @JsonProperty("cd_id")
    @GraphProperty("cd_id")
    public String cdId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("cas_reg_no")
    @GraphProperty("cas_reg_no")
    public String casRegNo;
    @JsonProperty("inchi")
    @GraphProperty("inchi")
    public String inchi;
    @JsonProperty("nostereo_inchi")
    @GraphProperty("nostereo_inchi")
    public String nostereoInchi;
    @JsonProperty("molfile")
    @GraphProperty("molfile")
    public String molfile;
    @JsonProperty("molimg")
    public String molimg;
    @JsonProperty("smiles")
    @GraphProperty("smiles")
    public String smiles;
    @JsonProperty("inchikey")
    @GraphProperty("inchi_key")
    public String inchiKey;
}
