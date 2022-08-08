package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"prodrug_ligand_id", "drug_ligand_id"})
public class ProDrug {
    @JsonProperty("prodrug_ligand_id")
    @GraphProperty("prodrug_ligand_id")
    public Long prodrugLigandId;
    @JsonProperty("drug_ligand_id")
    @GraphProperty("drug_ligand_id")
    public Long drugLigandId;
}
