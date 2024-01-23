package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("DrugLabel")
@JsonPropertyOrder({
        "PharmGKB ID", "Name", "Source", "Biomarker Flag", "Testing Level", "Has Prescribing Info", "Has Dosing Info",
        "Has Alternate Drug", "Has Other Prescribing Guidance", "Cancer Genome", "Prescribing", "Chemicals", "Genes",
        "Variants/Haplotypes", "Latest History Date (YYYY-MM-DD)"
})
public class DrugLabel {
    @JsonProperty("PharmGKB ID")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @JsonProperty("Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Source")
    @GraphProperty("source")
    public String source;
    @JsonProperty("Biomarker Flag")
    @GraphProperty("biomarker_flag")
    public String biomarkerFlag;
    @JsonProperty("Testing Level")
    @GraphProperty("testing_level")
    public String testingLevel;
    @JsonProperty("Has Prescribing Info")
    @GraphBooleanProperty(value = "has_prescribing_info", truthValue = "Prescribing Info")
    public String hasPrescribingInfo;
    @JsonProperty("Has Dosing Info")
    @GraphBooleanProperty(value = "has_dosing_info", truthValue = "Dosing Info")
    public String hasDosingInfo;
    @JsonProperty("Has Alternate Drug")
    @GraphBooleanProperty(value = "has_alternate_drug", truthValue = "Alternate Drug")
    public String hasAlternateDrug;
    @JsonProperty("Has Other Prescribing Guidance")
    @GraphProperty("has_other_prescribing_guidance") // TODO: boolean if truth value is known
    public String hasOtherPrescribingGuidance;
    @JsonProperty("Cancer Genome")
    @GraphBooleanProperty(value = "cancer_genome", truthValue = "Cancer Genome")
    public String cancerGenome;
    @JsonProperty("Prescribing")
    @GraphBooleanProperty(value = "prescribing", truthValue = "Prescribing")
    public String prescribing;
    @JsonProperty("Chemicals")
    public String chemicals;
    @JsonProperty("Genes")
    public String genes;
    @JsonProperty("Variants/Haplotypes")
    public String variantsHaplotypes;
    @JsonProperty("Latest History Date (YYYY-MM-DD)")
    public String latestHistoryDate;
}
