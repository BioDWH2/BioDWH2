package de.unibi.agbi.biodwh2.ncbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "#tax_id", "GeneID", "Symbol", "LocusTag", "Synonyms", "dbXrefs", "chromosome", "map_location", "description",
        "type_of_gene", "Symbol_from_nomenclature_authority", "Full_name_from_nomenclature_authority",
        "Nomenclature_status", "Other_designations", "Modification_date", "Feature_type"
})
public class GeneInfo {
    @JsonProperty("#tax_id")
    public String taxonomyId;
    @JsonProperty("GeneID")
    public String geneId;
    @JsonProperty("Symbol")
    public String symbol;
    @JsonProperty("LocusTag")
    public String locusTag;
    @JsonProperty("Synonyms")
    public String synonyms;
    @JsonProperty("dbXrefs")
    public String dbXrefs;
    @JsonProperty("chromosome")
    public String chromosome;
    @JsonProperty("map_location")
    public String mapLocation;
    @JsonProperty("description")
    public String description;
    @JsonProperty("type_of_gene")
    public String typeOfGene;
    @JsonProperty("Symbol_from_nomenclature_authority")
    public String symbolFromNomenclatureAuthority;
    @JsonProperty("Full_name_from_nomenclature_authority")
    public String fullNameFromNomenclatureAuthority;
    @JsonProperty("Nomenclature_status")
    public String nomenclatureStatus;
    @JsonProperty("Other_designations")
    public String otherDesignations;
    @JsonProperty("Modification_date")
    public String modificationDate;
    @JsonProperty("Feature_type")
    public String featureType;
}
