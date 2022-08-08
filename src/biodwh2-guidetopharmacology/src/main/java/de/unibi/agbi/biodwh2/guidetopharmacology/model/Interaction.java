package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "interaction_id", "ligand_id", "object_id", "type", "action", "action_comment", "species_id", "endogenous",
        "selective", "use_dependent", "voltage_dependent", "affinity_units", "affinity_high", "affinity_median",
        "affinity_low", "concentration_range", "affinity_voltage_high", "affinity_voltage_median",
        "affinity_voltage_low", "affinity_physiological_voltage", "rank", "selectivity", "original_affinity_low_nm",
        "original_affinity_median_nm", "original_affinity_high_nm", "original_affinity_units",
        "original_affinity_relation", "assay_description", "assay_conditions", "from_grac", "only_grac",
        "receptor_site", "ligand_context", "percent_activity", "assay_url", "primary_target", "target_ligand_id",
        "whole_organism_assay", "hide", "type_vector"
})
@GraphNodeLabel("Interaction")
public class Interaction {
    @JsonProperty("interaction_id")
    @GraphProperty("id")
    public Long id;
    @JsonProperty("ligand_id")
    public Long ligandId;
    @JsonProperty("object_id")
    public Long objectId;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("action")
    @GraphProperty("action")
    public String action;
    @JsonProperty("action_comment")
    @GraphProperty("action_comment")
    public String actionComment;
    @JsonProperty("species_id")
    public Long speciesId;
    @JsonProperty("endogenous")
    @GraphBooleanProperty(value = "endogenous", truthValue = "t")
    public String endogenous;
    @JsonProperty("selective")
    @GraphBooleanProperty(value = "selective", truthValue = "t")
    public String selective;
    @JsonProperty("use_dependent")
    @GraphBooleanProperty(value = "use_dependent", truthValue = "t")
    public String useDependent;
    @JsonProperty("voltage_dependent")
    @GraphBooleanProperty(value = "voltage_dependent", truthValue = "t")
    public String voltageDependent;
    @JsonProperty("affinity_units")
    @GraphProperty(value = "affinity_units", emptyPlaceholder = "-")
    public String affinityUnits;
    @JsonProperty("affinity_high")
    @GraphProperty("affinity_high")
    public String affinityHigh;
    @JsonProperty("affinity_median")
    @GraphProperty("affinity_median")
    public String affinityMedian;
    @JsonProperty("affinity_low")
    @GraphProperty("affinity_low")
    public String affinityLow;
    @JsonProperty("concentration_range")
    @GraphProperty("concentration_range")
    public String concentrationRange;
    @JsonProperty("affinity_voltage_high")
    @GraphProperty("affinity_voltage_high")
    public String affinityVoltageHigh;
    @JsonProperty("affinity_voltage_median")
    @GraphProperty("affinity_voltage_median")
    public String affinityVoltageMedian;
    @JsonProperty("affinity_voltage_low")
    @GraphProperty("affinity_voltage_low")
    public String affinityVoltageLow;
    @JsonProperty("affinity_physiological_voltage")
    @GraphBooleanProperty(value = "affinity_physiological_voltage", truthValue = "t")
    public String affinityPhysiologicalVoltage;
    @JsonProperty("rank")
    @GraphProperty("rank")
    public String rank;
    @JsonProperty("selectivity")
    @GraphProperty("selectivity")
    public String selectivity;
    @JsonProperty("original_affinity_low_nm")
    @GraphProperty("original_affinity_low_nm")
    public String originalAffinityLowNm;
    @JsonProperty("original_affinity_median_nm")
    @GraphProperty("original_affinity_median_nm")
    public String originalAffinityMedianNm;
    @JsonProperty("original_affinity_high_nm")
    @GraphProperty("original_affinity_high_nm")
    public String originalAffinityHighNm;
    @JsonProperty("original_affinity_units")
    @GraphProperty(value = "original_affinity_units", emptyPlaceholder = "-")
    public String originalAffinityUnits;
    @JsonProperty("original_affinity_relation")
    @GraphProperty("original_affinity_relation")
    public String originalAffinityRelation;
    @JsonProperty("assay_description")
    @GraphProperty("assay_description")
    public String assayDescription;
    @JsonProperty("assay_conditions")
    @GraphProperty("assay_conditions")
    public String assayConditions;
    @JsonProperty("from_grac")
    @GraphBooleanProperty(value = "from_grac", truthValue = "t")
    public String fromGrac;
    @JsonProperty("only_grac")
    @GraphBooleanProperty(value = "only_grac", truthValue = "t")
    public String onlyGrac;
    @JsonProperty("receptor_site")
    @GraphProperty("receptor_site")
    public String receptorSite;
    @JsonProperty("ligand_context")
    @GraphProperty("ligand_context")
    public String ligandContext;
    @JsonProperty("percent_activity")
    @GraphProperty("percent_activity")
    public String percentActivity;
    @JsonProperty("assay_url")
    @GraphProperty("assay_url")
    public String assayUrl;
    @JsonProperty("primary_target")
    @GraphBooleanProperty(value = "primary_target", truthValue = "t")
    public String primaryTarget;
    @JsonProperty("target_ligand_id")
    public Long targetLigandId;
    @JsonProperty("whole_organism_assay")
    @GraphBooleanProperty(value = "whole_organism_assay", truthValue = "t")
    public String wholeOrganismAssay;
    @JsonProperty("hide")
    @GraphBooleanProperty(value = "hide", truthValue = "t")
    public String hide;
    /**
     * Full text search vector for Postgresql. Can be ignored.
     */
    @JsonProperty("type_vector")
    public String typeVector;
}
