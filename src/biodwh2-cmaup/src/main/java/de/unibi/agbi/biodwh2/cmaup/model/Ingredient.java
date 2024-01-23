package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Ingredient_ID", "pref_name", "iupac_name", "chembl_id", "pubchem_cid", "zinc_id", "formula", "mw", "alogp",
        "mlogp", "xlogp", "hba", "hbd", "psa", "rotatable_bond", "rings", "heavy_atom", "lipinski_failure",
        "standard_inchi", "standard_inchi_key", "canonical_smiles"
})
@GraphNodeLabel("Ingredient")
public class Ingredient {
    @JsonProperty("Ingredient_ID")
    @GraphProperty("id")
    public String id;
    @JsonProperty("pref_name")
    @GraphProperty("pref_name")
    public String prefName;
    @JsonProperty("iupac_name")
    @GraphProperty("iupac_name")
    public String iupacName;
    @JsonProperty("chembl_id")
    @GraphProperty("chembl_id")
    public String chemblId;
    @JsonProperty("pubchem_cid")
    @GraphProperty(value = "pubchem_cid", emptyPlaceholder = {"NA", "n.a."})
    public String pubchemCid;
    @JsonProperty("zinc_id")
    @GraphProperty("zinc_id")
    public String zincId;
    @JsonProperty("formula")
    @GraphProperty("formula")
    public String formula;
    @JsonProperty("mw")
    @GraphProperty("mw")
    public String mw;
    @JsonProperty("alogp")
    @GraphProperty("alogp")
    public String alogp;
    @JsonProperty("mlogp")
    @GraphProperty("mlogp")
    public String mlogp;
    @JsonProperty("xlogp")
    @GraphProperty("xlogp")
    public String xlogp;
    @JsonProperty("hba")
    @GraphProperty("hba")
    public String hba;
    @JsonProperty("hbd")
    @GraphProperty("hbd")
    public String hbd;
    @JsonProperty("psa")
    @GraphProperty("psa")
    public String psa;
    @JsonProperty("rotatable_bond")
    @GraphProperty("rotatable_bond")
    public String rotatableBond;
    @JsonProperty("rings")
    @GraphProperty("rings")
    public String rings;
    @JsonProperty("heavy_atom")
    @GraphProperty("heavy_atom")
    public String heavyAtom;
    @JsonProperty("lipinski_failure")
    @GraphProperty("lipinski_failure")
    public String lipinskiFailure;
    @JsonProperty("standard_inchi")
    @GraphProperty("standard_inchi")
    public String standardInchi;
    @JsonProperty("standard_inchi_key")
    @GraphProperty("standard_inchi_key")
    public String standardInchiKey;
    @JsonProperty("canonical_smiles")
    @GraphProperty("canonical_smiles")
    public String canonicalSmiles;
}
