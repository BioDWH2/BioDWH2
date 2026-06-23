package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "compound_id", "location_in_ref", "source_id", "accession_number", "reference_name"})
public class Reference {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("location_in_ref")
    public String locationInRef;
    @JsonProperty("source_id")
    public Integer sourceId;
    @JsonProperty("accession_number")
    public String accessionNumber;
    @JsonProperty("reference_name")
    public String referenceName;
}
