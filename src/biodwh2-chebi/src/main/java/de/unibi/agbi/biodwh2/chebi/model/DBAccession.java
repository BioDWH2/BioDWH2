package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "compound_id", "accession_number", "type", "status_id", "source_id"})
public class DBAccession {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("accession_number")
    public String accessionNumber;
    @JsonProperty("type")
    public String type;
    @JsonProperty("status_id")
    public Integer statusId;
    @JsonProperty("source_id")
    public Integer sourceId;
}
