package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class DrugLabel {
    @Parsed(field = "PharmGKB ID")
    public String pharmgkb_accession_id;
    @Parsed(field = "Name")
    public String name;
    @Parsed(field = "Source")
    public String source;
    @Parsed(field = "Biomarker Flag")
    public String biomarker_flag;
    @Parsed(field = "Testing Level")
    public String testing_level;
    @Parsed(field = "Has Dosing Info")
    public String has_dosing_info;
    @Parsed(field = "Has Alternate Drug")
    public String has_alternate_drug;
    @Parsed(field = "Cancer Genome")
    public String cancer_genome;
}
