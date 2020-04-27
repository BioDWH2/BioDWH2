package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"DrugLabel"})
public class DrugLabel {
    @Parsed(field = "PharmGKB ID")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @Parsed(field = "Name")
    @GraphProperty("name")
    public String name;
    @Parsed(field = "Source")
    @GraphProperty("source")
    public String source;
    @Parsed(field = "Biomarker Flag")
    @GraphProperty("biomarker_flag")
    public String biomarkerFlag;
    @Parsed(field = "Testing Level")
    @GraphProperty("testing_level")
    public String testingLevel;
    @Parsed(field = "Has Dosing Info")
    public String hasDosingInfo;
    @Parsed(field = "Has Alternate Drug")
    public String hasAlternateDrug;
    @Parsed(field = "Cancer Genome")
    @GraphProperty("cancer_genome")
    public String cancerGenome;
}