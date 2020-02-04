package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class StudyParameters {
    @Parsed(field = "Study Parameters ID")
    public String studyParametersId;
    @Parsed(field = "Study Type")
    public String studyType;
    @Parsed(field = "Study Cases")
    public String studyCases;
    @Parsed(field = "Study Controls")
    public String studyControls;
    @Parsed(field = "Characteristics")
    public String characteristics;
    @Parsed(field = "Characteristics Type")
    public String characteristicsType;
    @Parsed(field = "Frequency In Cases")
    public String frequencyInCases;
    @Parsed(field = "Allele Of Frequency In Cases")
    public String alleleOfFrequencyInCases;
    @Parsed(field = "Frequency In Controls")
    public String frequencyInControls;
    @Parsed(field = "Allele Of Frequency In Controls")
    public String alleleOfFrequencyInControls;
    @Parsed(field = "P Value Operator")
    public String pValueOperator;
    @Parsed(field = "P Value")
    public String pValue;
    @Parsed(field = "Ratio Stat Type")
    public String ratioStatType;
    @Parsed(field = "Ratio Stat")
    public String ratioStat;
    @Parsed(field = "Confidence Interval Start")
    public String confidenceIntervalStart;
    @Parsed(field = "Confidence Interval Stop")
    public String confidenceIntervalStop;
    @Parsed(field = "Biogeographical Groups")
    public String biogeographicalGroups;
}
