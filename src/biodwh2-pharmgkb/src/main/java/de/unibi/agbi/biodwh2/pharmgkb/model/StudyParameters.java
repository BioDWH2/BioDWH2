package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels("StudyParameters")
public class StudyParameters {
    @Parsed(field = "Study Parameters ID")
    @GraphProperty("id")
    public Integer studyParametersId;
    @Parsed(field = "Study Type")
    @GraphProperty("study_type")
    public String studyType;
    @Parsed(field = "Study Cases")
    @GraphProperty("study_cases")
    public Integer studyCases;
    @Parsed(field = "Study Controls")
    @GraphProperty("study_controls")
    public Integer studyControls;
    @Parsed(field = "Characteristics")
    @GraphProperty("characteristics")
    public String characteristics;
    @Parsed(field = "Characteristics Type")
    @GraphProperty("characteristics_type")
    public String characteristicsType;
    @Parsed(field = "Frequency In Cases")
    @GraphProperty("frequency_in_cases")
    public String frequencyInCases;
    @Parsed(field = "Allele Of Frequency In Cases")
    @GraphProperty("allele_of_frequency_in_cases")
    public String alleleOfFrequencyInCases;
    @Parsed(field = "Frequency In Controls")
    @GraphProperty("frequency_in_controls")
    public String frequencyInControls;
    @Parsed(field = "Allele Of Frequency In Controls")
    @GraphProperty("allele_of_frequency_in_controls")
    public String alleleOfFrequencyInControls;
    @Parsed(field = "P Value Operator")
    @GraphProperty("p_value_operator")
    public String pValueOperator;
    @Parsed(field = "P Value")
    @GraphProperty("p_value")
    public String pValue;
    @Parsed(field = "Ratio Stat Type")
    @GraphProperty("ratio_stat_type")
    public String ratioStatType;
    @Parsed(field = "Ratio Stat")
    @GraphProperty("ratio_stat")
    public String ratioStat;
    @Parsed(field = "Confidence Interval Start")
    @GraphProperty("confidence_interval_start")
    public String confidenceIntervalStart;
    @Parsed(field = "Confidence Interval Stop")
    @GraphProperty("confidence_interval_stop")
    public String confidenceIntervalStop;
    @Parsed(field = "Biogeographical Groups")
    @GraphProperty("biogeographical_groups")
    public String biogeographicalGroups;
}
