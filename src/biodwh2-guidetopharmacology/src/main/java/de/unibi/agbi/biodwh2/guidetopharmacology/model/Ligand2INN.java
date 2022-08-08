package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ligand_id", "inn_number"})
public class Ligand2INN {
    @JsonProperty("ligand_id")
    public Long ligandId;
    @JsonProperty("inn_number")
    public Long innNumber;
}
