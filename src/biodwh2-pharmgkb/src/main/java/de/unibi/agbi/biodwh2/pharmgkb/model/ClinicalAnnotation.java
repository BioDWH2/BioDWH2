package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("ClinicalAnnotation")
@JsonPropertyOrder({
        "Clinical Annotation ID", "Variant/Haplotypes", "Gene", "Level of Evidence", "Level Override",
        "Level Modifiers", "Score", "Phenotype Category", "PMID Count", "Evidence Count", "Drug(s)", "Phenotype(s)",
        "Latest History Date (YYYY-MM-DD)", "URL", "Specialty Population"
})
public class ClinicalAnnotation {
    @JsonProperty("Clinical Annotation ID")
    @GraphProperty("id")
    public Integer clinicalAnnotationId;
    @JsonProperty("Variant/Haplotypes")
    public String variantHaplotypes;
    @JsonProperty("Gene")
    public String gene;
    @JsonProperty("Level of Evidence")
    @GraphProperty("level_of_evidence")
    public String levelOfEvidence;
    @JsonProperty("Level Override")
    @GraphProperty("level_override")
    public String levelOverride;
    @JsonProperty("Level Modifiers")
    @GraphProperty("level_modifiers")
    public String levelModifiers;
    @JsonProperty("Score")
    @GraphProperty("score")
    public String score;
    @JsonProperty("Phenotype Category")
    @GraphProperty("phenotype_category")
    public String phenotypeCategory;
    @JsonProperty("PMID Count")
    @GraphProperty("pmid_count")
    public Integer pmidCount;
    @JsonProperty("Evidence Count")
    @GraphProperty("evidence_count")
    public Integer evidenceCount;
    @JsonProperty("Drug(s)")
    public String drugs;
    @JsonProperty("Phenotype(s)")
    public String phenotypes;
    @JsonProperty("Latest History Date (YYYY-MM-DD)")
    @GraphProperty("latest_history_date")
    public String latestHistoryDate;
    @JsonProperty("URL")
    @GraphProperty("url")
    public String url;
    @JsonProperty("Specialty Population")
    @GraphProperty("specialty_population")
    public String specialtyPopulation;
}
