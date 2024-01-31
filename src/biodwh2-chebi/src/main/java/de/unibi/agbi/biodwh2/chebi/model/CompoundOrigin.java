package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "ID", "COMPOUND_ID", "SPECIES_TEXT", "SPECIES_ACCESSION", "COMPONENT_TEXT", "COMPONENT_ACCESSION",
        "STRAIN_TEXT", "STRAIN_ACCESSION", "SOURCE_TYPE", "SOURCE_ACCESSION", "COMMENTS"
})
public class CompoundOrigin {
    @JsonProperty("ID")
    public Integer id;
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("SPECIES_TEXT")
    public String speciesText;
    @JsonProperty("SPECIES_ACCESSION")
    public String speciesAccession;
    @JsonProperty("COMPONENT_TEXT")
    public String componentText;
    @JsonProperty("COMPONENT_ACCESSION")
    public String componentAccession;
    @JsonProperty("STRAIN_TEXT")
    public String strainText;
    @JsonProperty("STRAIN_ACCESSION")
    public String strainAccession;
    @JsonProperty("SOURCE_TYPE")
    public String sourceType;
    @JsonProperty("SOURCE_ACCESSION")
    public String sourceAccession;
    @JsonProperty("COMMENTS")
    public String comments;
}
