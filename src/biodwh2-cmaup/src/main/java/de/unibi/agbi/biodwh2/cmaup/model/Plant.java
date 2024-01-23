package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Plant_ID", "Plant_Name", "Species_Tax_ID", "Species_Name", "Genus_Tax_ID", "Genus_Name", "Family_Tax_ID",
        "Family_Name"
})
@GraphNodeLabel("Plant")
public class Plant {
    @JsonProperty("Plant_ID")
    @GraphProperty("id")
    public String id;
    @JsonProperty("Plant_Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Species_Tax_ID")
    @GraphNumberProperty(value = "species_tax_id", emptyPlaceholder = {"NA", "n.a."})
    public String speciesTaxId;
    @JsonProperty("Species_Name")
    @GraphProperty("species_name")
    public String speciesName;
    @JsonProperty("Genus_Tax_ID")
    @GraphProperty("genus_tax_id")
    public String genusTaxId;
    @JsonProperty("Genus_Name")
    @GraphProperty("genus_name")
    public String genusName;
    @JsonProperty("Family_Tax_ID")
    @GraphProperty("family_tax_id")
    public String familyTaxId;
    @JsonProperty("Family_Name")
    @GraphProperty("family_name")
    public String familyName;
}
