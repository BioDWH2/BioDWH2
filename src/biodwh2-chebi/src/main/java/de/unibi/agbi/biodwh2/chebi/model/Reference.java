package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"COMPOUND_ID", "REFERENCE_ID", "REFERENCE_DB_NAME", "LOCATION_IN_REF", "REFERENCE_NAME"})
public class Reference {
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("REFERENCE_ID")
    public String referenceId;
    @JsonProperty("REFERENCE_DB_NAME")
    public String referenceDbName;
    @JsonProperty("LOCATION_IN_REF")
    public String locationInRef;
    @JsonProperty("REFERENCE_NAME")
    public String referenceName;
}
