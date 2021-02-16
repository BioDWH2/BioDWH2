package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels("DrugLabel")
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
    @Parsed(field = "Has Prescribing Info")
    @GraphBooleanProperty(value = "has_prescribing_info", truthValue = "Prescribing Info")
    public String hasPrescribingInfo;
    @Parsed(field = "Has Dosing Info")
    @GraphBooleanProperty(value = "has_dosing_info", truthValue = "Dosing Info")
    public String hasDosingInfo;
    @Parsed(field = "Has Alternate Drug")
    @GraphBooleanProperty(value = "has_alternate_drug", truthValue = "Alternate Drug")
    public String hasAlternateDrug;
    @Parsed(field = "Cancer Genome")
    @GraphBooleanProperty(value = "cancer_genome", truthValue = "Cancer Genome")
    public String cancerGenome;
    @Parsed(field = "Prescribing")
    @GraphBooleanProperty(value = "prescribing", truthValue = "Prescribing")
    public String prescribing;
    @Parsed(field = "Chemicals")
    public String chemicals;
    @Parsed(field = "Genes")
    public String genes;
    @Parsed(field = "Variants/Haplotypes")
    public String variantsHaplotypes;
    @Parsed(field = "Latest History Date (YYYY-MM-DD)")
    public String latestHistoryDate;
}
