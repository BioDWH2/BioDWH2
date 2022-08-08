package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "ligand_id", "hydrogen_bond_acceptors", "hydrogen_bond_donors", "rotatable_bonds_count",
        "topological_polar_surface_area", "molecular_weight", "xlogp", "lipinski_s_rule_of_five"
})
public class LigandPhysChem {
    @JsonProperty("ligand_id")
    public Long ligandId;
    @JsonProperty("hydrogen_bond_acceptors")
    @GraphProperty("hydrogen_bond_acceptors")
    public Integer hydrogenBondAcceptors;
    @JsonProperty("hydrogen_bond_donors")
    @GraphProperty("hydrogen_bond_donors")
    public Integer hydrogenBondDonors;
    @JsonProperty("rotatable_bonds_count")
    @GraphProperty("rotatable_bonds_count")
    public Integer rotatableBondsCount;
    @JsonProperty("topological_polar_surface_area")
    @GraphProperty("topological_polar_surface_area")
    public String topologicalPolarSurfaceArea;
    @JsonProperty("molecular_weight")
    @GraphProperty("molecular_weight")
    public String molecularWeight;
    @JsonProperty("xlogp")
    @GraphProperty("xlogp")
    public String xlogp;
    @JsonProperty("lipinski_s_rule_of_five")
    @GraphProperty("lipinskis_rule_of_five")
    public Integer lipinskisRuleOfFive;
}
