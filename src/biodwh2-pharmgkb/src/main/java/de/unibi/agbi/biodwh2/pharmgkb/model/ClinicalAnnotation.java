package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels("ClinicalAnnotation")
public class ClinicalAnnotation {
    @Parsed(field = "Clinical Annotation ID")
    @GraphProperty("id")
    public Integer clinicalAnnotationId;
    @Parsed(field = "Variant/Haplotypes")
    public String variantHaplotypes;
    @Parsed(field = "Gene")
    public String gene;
    @Parsed(field = "Level of Evidence")
    @GraphProperty("level_of_evidence")
    public String levelOfEvidence;
    @Parsed(field = "Level Override")
    @GraphProperty("level_override")
    public String levelOverride;
    @Parsed(field = "Level Modifiers")
    @GraphProperty("level_modifiers")
    public String levelModifiers;
    @Parsed(field = "Score")
    @GraphProperty("score")
    public String score;
    @Parsed(field = "Phenotype Category")
    @GraphProperty("phenotype_category")
    public String phenotypeCategory;
    @Parsed(field = "PMID Count")
    @GraphProperty("pmid_count")
    public Integer pmidCount;
    @Parsed(field = "Evidence Count")
    @GraphProperty("evidence_count")
    public Integer evidenceCount;
    @Parsed(field = "Drug(s)")
    public String drugs;
    @Parsed(field = "Phenotype(s)")
    public String phenotypes;
    @Parsed(field = "Latest History Date (YYYY-MM-DD)")
    @GraphProperty("latest_history_date")
    public String latestHistoryDate;
    @Parsed(field = "URL")
    @GraphProperty("url")
    public String url;
    @Parsed(field = "Specialty Population")
    @GraphProperty("specialty_population")
    public String specialtyPopulation;
}
