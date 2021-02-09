package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Mixture"})
public final class Mixture {
    @GraphProperty("name")
    public String name;
    @GraphProperty("ingredients")
    public String ingredients;
    @GraphProperty("supplemental_ingredients")
    @JsonProperty("supplemental-ingredients")
    public String supplementalIngredients;
}
