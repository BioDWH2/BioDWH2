package de.unibi.agbi.biodwh2.unii.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Name", "TYPE", "UNII", "Display Name"})
public class UNIIEntry {
    @JsonProperty("Name")
    public String name;
    /*
     * of = name identified as having official status
     * sys = Systematic Name  (a new value - many existing SY terms will become SN)
     * cn = Common Name
     * cd = Code
     * bn = Brand Name
     */
    @JsonProperty("TYPE")
    public String type;
    /*
     * Unique Ingredient Identifier - a non-proprietary, free, unique, unambiguous, nonsemantic, alphanumeric
     * identifier based on a substance's molecular structure and/or descriptive information.
     * http://www.fda.gov/ForIndustry/DataStandards/SubstanceRegistrationSystem-UniqueIngredientIdentifierUNII/default.html
     */
    @JsonProperty("UNII")
    public String unii;
    @JsonProperty("Display Name")
    public String displayName;
}
