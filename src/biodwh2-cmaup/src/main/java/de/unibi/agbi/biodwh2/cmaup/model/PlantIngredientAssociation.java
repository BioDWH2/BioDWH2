package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Plant_ID", "Ingredient_ID"})
public class PlantIngredientAssociation {
    @JsonProperty("Plant_ID")
    public String plantId;
    @JsonProperty("Ingredient_ID")
    public String ingredientId;
}
