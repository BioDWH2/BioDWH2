package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.cmaup.etl.CMAUPGraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Ingredient_ID", "Target_ID", "Activity_Type", "Activity_Relationship", "Activity_Value", "Activity_Unit",
        "Reference_ID", "Reference_ID_Type", "Reference_ID_Others"
})
@GraphEdgeLabel(CMAUPGraphExporter.TARGETS_LABEL)
public class IngredientTargetAssociation {
    @JsonProperty("Ingredient_ID")
    public String ingredientId;
    @JsonProperty("Target_ID")
    public String targetId;
    @JsonProperty("Activity_Type")
    @GraphProperty("activity_type")
    public String activityType;
    @JsonProperty("Activity_Relationship")
    @GraphProperty("activity_relationship")
    public String activityRelationship;
    @JsonProperty("Activity_Value")
    @GraphProperty("activity_value")
    public String activityValue;
    @JsonProperty("Activity_Unit")
    @GraphProperty("activity_unit")
    public String activityUnit;
    @JsonProperty("Reference_ID")
    @GraphProperty("reference_id")
    public String referenceId;
    @JsonProperty("Reference_ID_Type")
    @GraphProperty("reference_id_type")
    public String referenceIdType;
    @JsonProperty("Reference_ID_Others")
    @GraphProperty("reference_id_others")
    public String referenceIdOthers;
}
