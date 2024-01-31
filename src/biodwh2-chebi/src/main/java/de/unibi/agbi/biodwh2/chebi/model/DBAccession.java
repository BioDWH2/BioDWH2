package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "COMPOUND_ID", "SOURCE", "TYPE", "ACCESSION_NUMBER"})
public class DBAccession {
    @JsonProperty("ID")
    public Integer id;
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("SOURCE")
    public String source;
    @JsonProperty("TYPE")
    public String type;
    @JsonProperty("ACCESSION_NUMBER")
    public String accessionNumber;
}
