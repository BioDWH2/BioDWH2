package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.cmaup.etl.CMAUPGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "np_id", "pref_name", "iupac_name", "chembl_id", "pubchem_cid", "MW", "LogS", "LogD", "LogP", "nHA", "nHD",
        "TPSA", "nRot", "nRing", "InChI", "InChIKey", "SMILES"
})
@GraphNodeLabel(CMAUPGraphExporter.INGREDIENT_LABEL)
public class Ingredient {
    @JsonProperty("np_id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("pref_name")
    @GraphProperty(value = "pref_name", emptyPlaceholder = {"NA", "n.a."})
    public String prefName;
    @JsonProperty("iupac_name")
    @GraphProperty(value = "iupac_name", emptyPlaceholder = {"NA", "n.a."})
    public String iupacName;
    @JsonProperty("chembl_id")
    @GraphProperty(value = "chembl_id", emptyPlaceholder = {"NA", "n.a."})
    public String chemblId;
    @JsonProperty("pubchem_cid")
    @GraphArrayProperty(value = "pubchem_cid", emptyPlaceholder = {"NA", "n.a."}, type = GraphArrayProperty.Type.Int)
    public String pubchemCid;
    @JsonProperty("MW")
    @GraphProperty(value = "mw", emptyPlaceholder = {"NA", "n.a."})
    public String mw;
    @JsonProperty("LogS")
    @GraphProperty(value = "logs", emptyPlaceholder = {"NA", "n.a."})
    public String logS;
    @JsonProperty("LogD")
    @GraphProperty(value = "logd", emptyPlaceholder = {"NA", "n.a."})
    public String logD;
    @JsonProperty("LogP")
    @GraphProperty(value = "logp", emptyPlaceholder = {"NA", "n.a."})
    public String logp;
    @JsonProperty("nHA")
    @GraphProperty(value = "nha", emptyPlaceholder = {"NA", "n.a."})
    public String nha;
    @JsonProperty("nHD")
    @GraphProperty(value = "nhd", emptyPlaceholder = {"NA", "n.a."})
    public String nhd;
    @JsonProperty("TPSA")
    @GraphProperty(value = "tpsa", emptyPlaceholder = {"NA", "n.a."})
    public String tpsa;
    @JsonProperty("nRot")
    @GraphNumberProperty(value = "rotatable_bonds", emptyPlaceholder = {"NA", "n.a."})
    public String rotatableBonds;
    @JsonProperty("nRing")
    @GraphNumberProperty(value = "rings", emptyPlaceholder = {"NA", "n.a."})
    public String rings;
    @JsonProperty("InChI")
    @GraphProperty("inchi")
    public String inchi;
    @JsonProperty("InChIKey")
    @GraphProperty("inchi_key")
    public String inchiKey;
    @JsonProperty("SMILES")
    @GraphProperty("smiles")
    public String smiles;
}
