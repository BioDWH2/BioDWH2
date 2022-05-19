package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Ingredient_ID", "Target_ID", "Activity_Type", "Activity_Relationship", "Activity_Value", "Activity_Unit",
        "Reference_ID", "Reference_ID_Type", "Reference_ID_Others"
})
public class IngredientTargetAssociation {
    @JsonProperty("Ingredient_ID")
    public String ingredientId;
    @JsonProperty("Target_ID")
    public String targetId;
    @JsonProperty("Activity_Type")
    public String activityType;
    @JsonProperty("Activity_Relationship")
    public String activityRelationship;
    @JsonProperty("Activity_Value")
    public String activityValue;
    @JsonProperty("Activity_Unit")
    public String activityUnit;
    @JsonProperty("Reference_ID")
    public String referenceId;
    @JsonProperty("Reference_ID_Type")
    public String referenceIdType;
    @JsonProperty("Reference_ID_Others")
    public String referenceIdOthers;
}
