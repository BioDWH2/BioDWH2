package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

import java.util.Objects;

@GraphNodeLabel("Mixture")
public final class Mixture {
    @GraphProperty("name")
    public String name;
    @GraphProperty("ingredients")
    public String ingredients;
    @GraphProperty("supplemental_ingredients")
    @JsonProperty("supplemental-ingredients")
    public String supplementalIngredients;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Mixture mixture = (Mixture) o;
        return Objects.equals(name, mixture.name) && Objects.equals(ingredients, mixture.ingredients) && Objects.equals(
                supplementalIngredients, mixture.supplementalIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, supplementalIngredients);
    }
}
