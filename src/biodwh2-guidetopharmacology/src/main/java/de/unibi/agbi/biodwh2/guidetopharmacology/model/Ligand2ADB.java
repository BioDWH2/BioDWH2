package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ligand_id", "adb_id"})
public class Ligand2ADB {
    @JsonProperty("ligand_id")
    public Long ligandId;
    @JsonProperty("adb_id")
    public Long adbId;
}
