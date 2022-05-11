package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Ingredient_id", "Ingredient_name", "Alias", "Ingredient_formula", "Ingredient_Smile", "Ingredient_weight",
        "OB_score", "CAS_id", "SymMap_id", "TCMID_id", "TCMSP_id", "TCM-ID_id", "PubChem_id", "DrugBank_id", ""
})
@GraphNodeLabel("Ingredient")
public class Ingredient {
    @JsonProperty("Ingredient_id")
    @GraphProperty("id")
    public String ingredientId;
    @JsonProperty("Ingredient_name")
    @GraphProperty("name")
    public String ingredientName;
    @JsonProperty("Alias")
    @GraphArrayProperty(value = "alias", arrayDelimiter = "; ", emptyPlaceholder = "NA")
    public String alias;
    @JsonProperty("Ingredient_formula")
    @GraphProperty(value = "ingredient_formula", emptyPlaceholder = "NA")
    public String ingredientFormula;
    @JsonProperty("Ingredient_Smile")
    @GraphProperty(value = "ingredient_smile", emptyPlaceholder = "NA")
    public String ingredientSmile;
    @JsonProperty("Ingredient_weight")
    @GraphProperty(value = "ingredient_weight", emptyPlaceholder = "NA")
    public String ingredientWeight;
    @JsonProperty("OB_score")
    @GraphProperty(value = "ob_sore", emptyPlaceholder = "NA")
    public String obScore;
    @JsonProperty("CAS_id")
    @GraphProperty(value = "cas_id", emptyPlaceholder = "NA")
    public String casId;
    @JsonProperty("SymMap_id")
    @GraphProperty(value = "symmap_class", emptyPlaceholder = "NA")
    public String symMapId;
    @JsonProperty("TCMID_id")
    @GraphProperty(value = "tcmid_class", emptyPlaceholder = "NA")
    public String tcmidId;
    @JsonProperty("TCMSP_id")
    @GraphProperty(value = "tcmsp_id", emptyPlaceholder = "NA")
    public String tcmspId;
    @JsonProperty("TCM_ID_id")
    @GraphProperty(value = "tcm_id_class", emptyPlaceholder = "NA")
    public String tcmIdId;
    @JsonProperty("PubChem_id")
    @GraphProperty(value = "pubchem_id", emptyPlaceholder = "NA")
    public String pubChemId;
    @JsonProperty("DrugBank_id")
    @GraphProperty(value = "drugbank_id", emptyPlaceholder = "NA")
    public String drugBankId;
    @JsonProperty("")
    public String empty;
}
