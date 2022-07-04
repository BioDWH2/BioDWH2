package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "variant_id", "object_id", "description", "type", "species_id", "amino_acids", "amino_acid_change",
        "validation", "global_maf", "subpop_maf", "minor_allele_count", "frequency_comment", "nucleotide_change",
        "description_vector"
})
@GraphNodeLabel("Variant")
public class Variant {
    @JsonProperty("variant_id")
    @GraphProperty("id")
    public Long variantId;
    @JsonProperty("object_id")
    public Long objectId;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("species_id")
    public Long speciesId;
    @JsonProperty("amino_acids")
    @GraphProperty("amino_acids")
    public Long aminoAcids;
    @JsonProperty("amino_acid_change")
    @GraphProperty("amino_acid_change")
    public String aminoAcidChange;
    @JsonProperty("validation")
    @GraphProperty("validation")
    public String validation;
    @JsonProperty("global_maf")
    @GraphProperty("global_maf")
    public String globalMaf;
    @JsonProperty("subpop_maf")
    @GraphProperty("subpop_maf")
    public String subpopMaf;
    @JsonProperty("minor_allele_count")
    @GraphProperty("minor_allele_count")
    public String minorAlleleCount;
    @JsonProperty("frequency_comment")
    @GraphProperty("frequency_comment")
    public String frequencyComment;
    @JsonProperty("nucleotide_change")
    @GraphProperty("nucleotide_change")
    public String nucleotideChange;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("description_vector")
    public String descriptionVector;
}
