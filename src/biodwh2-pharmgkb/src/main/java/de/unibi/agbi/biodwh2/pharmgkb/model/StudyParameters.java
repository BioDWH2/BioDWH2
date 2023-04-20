package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("StudyParameters")
@JsonPropertyOrder({
        "Study Parameters ID", "Variant Annotation ID", "Study Type", "Study Cases", "Study Controls",
        "Characteristics", "Characteristics Type", "Frequency In Cases", "Allele Of Frequency In Cases",
        "Frequency In Controls", "Allele Of Frequency In Controls", "P Value", "Ratio Stat Type", "Ratio Stat",
        "Confidence Interval Start", "Confidence Interval Stop", "Biogeographical Groups"
})
public class StudyParameters {
    @JsonProperty("Study Parameters ID")
    @GraphProperty("id")
    public Integer studyParametersId;
    @JsonProperty("Variant Annotation ID")
    public Integer variantAnnotationId;
    @JsonProperty("Study Type")
    @GraphProperty("study_type")
    public String studyType;
    @JsonProperty("Study Cases")
    @GraphProperty("study_cases")
    public Integer studyCases;
    @JsonProperty("Study Controls")
    @GraphProperty("study_controls")
    public Integer studyControls;
    @JsonProperty("Characteristics")
    @GraphProperty("characteristics")
    public String characteristics;
    @JsonProperty("Characteristics Type")
    @GraphProperty("characteristics_type")
    public String characteristicsType;
    @JsonProperty("Frequency In Cases")
    @GraphProperty("frequency_in_cases")
    public String frequencyInCases;
    @JsonProperty("Allele Of Frequency In Cases")
    @GraphProperty("allele_of_frequency_in_cases")
    public String alleleOfFrequencyInCases;
    @JsonProperty("Frequency In Controls")
    @GraphProperty("frequency_in_controls")
    public String frequencyInControls;
    @JsonProperty("Allele Of Frequency In Controls")
    @GraphProperty("allele_of_frequency_in_controls")
    public String alleleOfFrequencyInControls;
    @JsonProperty("P Value")
    @GraphProperty("p_value")
    public String pValue;
    @JsonProperty("Ratio Stat Type")
    @GraphProperty("ratio_stat_type")
    public String ratioStatType;
    @JsonProperty("Ratio Stat")
    @GraphProperty("ratio_stat")
    public String ratioStat;
    @JsonProperty("Confidence Interval Start")
    @GraphProperty("confidence_interval_start")
    public String confidenceIntervalStart;
    @JsonProperty("Confidence Interval Stop")
    @GraphProperty("confidence_interval_stop")
    public String confidenceIntervalStop;
    @JsonProperty("Biogeographical Groups")
    @GraphProperty("biogeographical_groups")
    public String biogeographicalGroups;
}
