package de.unibi.agbi.biodwh2.gencc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "uuid", "gene_curie", "gene_symbol", "disease_curie", "disease_title", "disease_original_curie",
        "disease_original_title", "classification_curie", "classification_title", "moi_curie", "moi_title",
        "submitter_curie", "submitter_title", "submitted_as_hgnc_id", "submitted_as_hgnc_symbol",
        "submitted_as_disease_id", "submitted_as_disease_name", "submitted_as_moi_id", "submitted_as_moi_name",
        "submitted_as_submitter_id", "submitted_as_submitter_name", "submitted_as_classification_id",
        "submitted_as_classification_name", "submitted_as_date", "submitted_as_public_report_url", "submitted_as_notes",
        "submitted_as_pmids", "submitted_as_assertion_criteria_url", "submitted_as_submission_id", "submitted_run_date"
})
public class Entry {
    @JsonProperty("uuid")
    @GraphProperty("id")
    public String uuid;
    @JsonProperty("gene_curie")
    public String geneCurie;
    @JsonProperty("gene_symbol")
    public String geneSymbol;
    @JsonProperty("disease_curie")
    public String diseaseCurie;
    @JsonProperty("disease_title")
    public String diseaseTitle;
    @JsonProperty("disease_original_curie")
    @GraphProperty("disease_original_curie")
    public String diseaseOriginalCurie;
    @JsonProperty("disease_original_title")
    @GraphProperty("disease_original_title")
    public String diseaseOriginalTitle;
    @JsonProperty("classification_curie")
    @GraphProperty("classification_curie")
    public String classificationCurie;
    @JsonProperty("classification_title")
    @GraphProperty("classification_title")
    public String classificationTitle;
    @JsonProperty("moi_curie")
    @GraphProperty("moi_curie")
    public String moiCurie;
    @JsonProperty("moi_title")
    @GraphProperty("moi_title")
    public String moiTitle;
    @JsonProperty("submitter_curie")
    @GraphProperty("submitter_curie")
    public String submitterCurie;
    @JsonProperty("submitter_title")
    @GraphProperty("submitter_title")
    public String submitterTitle;
    @JsonProperty("submitted_as_hgnc_id")
    @GraphProperty("submitted_as_hgnc_id")
    public String submittedAsHgncId;
    @JsonProperty("submitted_as_hgnc_symbol")
    @GraphProperty("submitted_as_hgnc_symbol")
    public String submittedAsHgncSymbol;
    @JsonProperty("submitted_as_disease_id")
    @GraphProperty("submitted_as_disease_id")
    public String submittedAsDiseaseId;
    @JsonProperty("submitted_as_disease_name")
    @GraphProperty("submitted_as_disease_name")
    public String submittedAsDiseaseName;
    @JsonProperty("submitted_as_moi_id")
    @GraphProperty("submitted_as_moi_id")
    public String submittedAsMoiId;
    @JsonProperty("submitted_as_moi_name")
    @GraphProperty("submitted_as_moi_name")
    public String submittedAsMoiName;
    @JsonProperty("submitted_as_submitter_id")
    @GraphProperty("submitted_as_submitter_id")
    public String submittedAsSubmitterId;
    @JsonProperty("submitted_as_submitter_name")
    @GraphProperty("submitted_as_submitter_name")
    public String submittedAsSubmitterName;
    @JsonProperty("submitted_as_classification_id")
    @GraphProperty("submitted_as_classification_id")
    public String submittedAsClassificationId;
    @JsonProperty("submitted_as_classification_name")
    @GraphProperty("submitted_as_classification_name")
    public String submittedAsClassificationName;
    @JsonProperty("submitted_as_date")
    @GraphProperty("submitted_as_date")
    public String submittedAsDate;
    @JsonProperty("submitted_as_public_report_url")
    @GraphProperty("submitted_as_public_report_url")
    public String submittedAsPublicReportUrl;
    @JsonProperty("submitted_as_notes")
    @GraphProperty("submitted_as_notes")
    public String submittedAsNotes;
    @JsonProperty("submitted_as_pmids")
    public String submittedAsPmids;
    @JsonProperty("submitted_as_assertion_criteria_url")
    @GraphProperty("submitted_as_assertion_criteria_url")
    public String submittedAsAssertionCriteriaUrl;
    @JsonProperty("submitted_as_submission_id")
    @GraphProperty("submitted_as_submission_id")
    public String submittedAsSubmissionId;
    @JsonProperty("submitted_run_date")
    @GraphProperty("submitted_run_date")
    public String submittedRunDate;
}
