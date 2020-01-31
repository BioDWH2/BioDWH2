package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class StudyParameters {
    @Parsed(field = "Study Parameters ID")
    public String study_parameters_id;
    @Parsed(field = "Study Type")
    public String study_type;
    @Parsed(field = "Study Cases")
    public String study_cases;
    @Parsed(field = "Study Controls")
    public String study_controls;
    @Parsed(field = "Characteristics")
    public String characteristics;
    @Parsed(field = "Characteristics Type")
    public String characteristics_type;
    @Parsed(field = "Frequency In Cases")
    public String frequency_in_cases;
    @Parsed(field = "Allele Of Frequency In Cases")
    public String allele_of_frequency_in_cases;
    @Parsed(field = "Frequency In Controls")
    public String frequency_in_controls;
    @Parsed(field = "Allele Of Frequency In Controls")
    public String allele_of_frequency_in_controls;
    @Parsed(field = "P Value Operator")
    public String p_value_operator;
    @Parsed(field = "P Value")
    public String p_value;
    @Parsed(field = "Ratio Stat Type")
    public String ratio_stat_type;
    @Parsed(field = "Ratio Stat")
    public String ratio_stat;
    @Parsed(field = "Confidence Interval Start")
    public String confidence_interval_start;
    @Parsed(field = "Confidence Interval Stop")
    public String confidence_interval_stop;
    @Parsed(field = "Race(s)")
    public String races;
}
