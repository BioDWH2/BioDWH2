package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class DrugLabel {
    @Parsed(field = "PharmGKB ID")
    public String pharmgkbAccessionId;
    @Parsed(field = "Name")
    public String name;
    @Parsed(field = "Source")
    public String source;
    @Parsed(field = "Biomarker Flag")
    public String biomarkerFlag;
    @Parsed(field = "Testing Level")
    public String testingLevel;
    @Parsed(field = "Has Dosing Info")
    public String hasDosingInfo;
    @Parsed(field = "Has Alternate Drug")
    public String hasAlternateDrug;
    @Parsed(field = "Cancer Genome")
    public String cancerGenome;
}
