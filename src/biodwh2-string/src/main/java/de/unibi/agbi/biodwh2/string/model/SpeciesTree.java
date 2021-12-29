package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#taxon_id", "parent_taxon_id", "taxon_name", "is_STRING_species"})
public class SpeciesTree {
    @JsonProperty("#taxon_id")
    public Integer taxonId;
    @JsonProperty("parent_taxon_id")
    public Integer parentTaxonId;
    @JsonProperty("taxon_name")
    public String taxonName;
    @JsonProperty("is_STRING_species")
    public String isSTRINGSpecies;
}
