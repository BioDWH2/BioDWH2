package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"adb_id", "drug_name", "drug_class", "high_dev_phase", "institute", "ligand_id"})
@GraphNodeLabel("AntibioticDB")
public class AntibioticDB {
    @JsonProperty("adb_id")
    @GraphProperty("id")
    public Long id;
    @JsonProperty("drug_name")
    @GraphProperty("drug_name")
    public String drugName;
    @JsonProperty("drug_class")
    @GraphProperty("drug_class")
    public String drugClass;
    @JsonProperty("high_dev_phase")
    @GraphProperty("high_dev_phase")
    public String highDevPhase;
    @JsonProperty("institute")
    @GraphProperty("institute")
    public String institute;
    /**
     * Not used as the table "ligand2adb" already covers this
     */
    @JsonProperty("ligand_id")
    public Long ligandId;
}
