package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "nct_id", "nlm_download_date_description", "study_first_submitted_date", "results_first_submitted_date",
        "disposition_first_submitted_date", "last_update_submitted_date", "study_first_submitted_qc_date",
        "study_first_posted_date", "study_first_posted_date_type", "results_first_submitted_qc_date",
        "results_first_posted_date", "results_first_posted_date_type", "disposition_first_submitted_qc_date",
        "disposition_first_posted_date", "disposition_first_posted_date_type", "last_update_submitted_qc_date",
        "last_update_posted_date", "last_update_posted_date_type", "start_month_year", "start_date_type", "start_date",
        "verification_month_year", "verification_date", "completion_month_year", "completion_date_type",
        "completion_date", "primary_completion_month_year", "primary_completion_date_type", "primary_completion_date",
        "target_duration", "study_type", "acronym", "baseline_population", "brief_title", "official_title",
        "overall_status", "last_known_status", "phase", "enrollment", "enrollment_type", "source",
        "limitations_and_caveats", "number_of_arms", "number_of_groups", "why_stopped", "has_expanded_access",
        "expanded_access_type_individual", "expanded_access_type_intermediate", "expanded_access_type_treatment",
        "has_dmc", "is_fda_regulated_drug", "is_fda_regulated_device", "is_unapproved_device", "is_ppsd",
        "is_us_export", "biospec_retention", "biospec_description", "ipd_time_frame", "ipd_access_criteria", "ipd_url",
        "plan_to_share_ipd", "plan_to_share_ipd_description", "created_at", "updated_at"
})
public class Study {
    @JsonProperty("nct_id")
    @GraphProperty("nct_id")
    public String nctId;
    @JsonProperty("nlm_download_date_description")
    @GraphProperty("nlm_download_date_description")
    public String nlmDownloadDateDescription;
    @JsonProperty("study_first_submitted_date")
    @GraphProperty("study_first_submitted_date")
    public String studyFirstSubmittedDate;
    @JsonProperty("results_first_submitted_date")
    @GraphProperty("results_first_submitted_date")
    public String resultsFirstSubmittedDate;
    @JsonProperty("disposition_first_submitted_date")
    @GraphProperty("disposition_first_submitted_date")
    public String dispositionFirstSubmittedDate;
    @JsonProperty("last_update_submitted_date")
    @GraphProperty("last_update_submitted_date")
    public String lastUpdateSubmittedDate;
    @JsonProperty("study_first_submitted_qc_date")
    @GraphProperty("study_first_submitted_qc_date")
    public String studyFirstSubmittedQcDate;
    @JsonProperty("study_first_posted_date")
    @GraphProperty("study_first_posted_date")
    public String studyFirstPostedDate;
    @JsonProperty("study_first_posted_date_type")
    @GraphProperty("study_first_posted_date_type")
    public String studyFirstPostedDateType;
    @JsonProperty("results_first_submitted_qc_date")
    @GraphProperty("results_first_submitted_qc_date")
    public String resultsFirstSubmittedQcDate;
    @JsonProperty("results_first_posted_date")
    @GraphProperty("results_first_posted_date")
    public String resultsFirstPostedDate;
    @JsonProperty("results_first_posted_date_type")
    @GraphProperty("results_first_posted_date_type")
    public String resultsFirstPostedDateType;
    @JsonProperty("disposition_first_submitted_qc_date")
    @GraphProperty("disposition_first_submitted_qc_date")
    public String dispositionFirstSubmittedQcDate;
    @JsonProperty("disposition_first_posted_date")
    @GraphProperty("disposition_first_posted_date")
    public String dispositionFirstPostedDate;
    @JsonProperty("disposition_first_posted_date_type")
    @GraphProperty("disposition_first_posted_date_type")
    public String dispositionFirstPostedDateType;
    @JsonProperty("last_update_submitted_qc_date")
    @GraphProperty("last_update_submitted_qc_date")
    public String lastUpdateSubmittedQcDate;
    @JsonProperty("last_update_posted_date")
    @GraphProperty("last_update_posted_date")
    public String lastUpdatePostedDate;
    @JsonProperty("last_update_posted_date_type")
    @GraphProperty("last_update_posted_date_type")
    public String lastUpdatePostedDateType;
    @JsonProperty("start_month_year")
    @GraphProperty("start_month_year")
    public String startMonthYear;
    @JsonProperty("start_date_type")
    @GraphProperty("start_date_type")
    public String startDateType;
    @JsonProperty("start_date")
    @GraphProperty("start_date")
    public String startDate;
    @JsonProperty("verification_month_year")
    @GraphProperty("verification_month_year")
    public String verificationMonthYear;
    @JsonProperty("verification_date")
    @GraphProperty("verification_date")
    public String verificationDate;
    @JsonProperty("completion_month_year")
    @GraphProperty("completion_month_year")
    public String completionMonthYear;
    @JsonProperty("completion_date_type")
    @GraphProperty("completion_date_type")
    public String completionDateType;
    @JsonProperty("completion_date")
    @GraphProperty("completion_date")
    public String completionDate;
    @JsonProperty("primary_completion_month_year")
    @GraphProperty("primary_completion_month_year")
    public String primaryCompletionMonthYear;
    @JsonProperty("primary_completion_date_type")
    @GraphProperty("primary_completion_date_type")
    public String primaryCompletionDateType;
    @JsonProperty("primary_completion_date")
    @GraphProperty("primary_completion_date")
    public String primaryCompletionDate;
    @JsonProperty("target_duration")
    @GraphProperty("target_duration")
    public String targetDuration;
    @JsonProperty("study_type")
    @GraphProperty("study_type")
    public String studyType;
    @JsonProperty("acronym")
    @GraphProperty("acronym")
    public String acronym;
    @JsonProperty("baseline_population")
    @GraphProperty("baseline_population")
    public String baselinePopulation;
    @JsonProperty("brief_title")
    @GraphProperty("brief_title")
    public String briefTitle;
    @JsonProperty("official_title")
    @GraphProperty("official_title")
    public String officialTitle;
    @JsonProperty("overall_status")
    @GraphProperty("overall_status")
    public String overallStatus;
    @JsonProperty("last_known_status")
    @GraphProperty("last_known_status")
    public String lastKnownStatus;
    @JsonProperty("phase")
    @GraphProperty("phase")
    public String phase;
    @JsonProperty("enrollment")
    @GraphProperty("enrollment")
    public Integer enrollment;
    @JsonProperty("enrollment_type")
    @GraphProperty("enrollment_type")
    public String enrollmentType;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
    @JsonProperty("limitations_and_caveats")
    @GraphProperty("limitations_and_caveats")
    public String limitationsAndCaveats;
    @JsonProperty("number_of_arms")
    @GraphProperty("number_of_arms")
    public Integer numberOfArms;
    @JsonProperty("number_of_groups")
    @GraphProperty("number_of_groups")
    public Integer numberOfGroups;
    @JsonProperty("why_stopped")
    @GraphProperty("why_stopped")
    public String whyStopped;
    @JsonProperty("has_expanded_access")
    @GraphBooleanProperty(value = "has_expanded_access", truthValue = "t")
    public String hasExpandedAccess;
    @JsonProperty("expanded_access_type_individual")
    @GraphBooleanProperty(value = "expanded_access_type_individual", truthValue = "t")
    public String expandedAccessTypeIndividual;
    @JsonProperty("expanded_access_type_intermediate")
    @GraphBooleanProperty(value = "expanded_access_type_intermediate", truthValue = "t")
    public String expandedAccessTypeIntermediate;
    @JsonProperty("expanded_access_type_treatment")
    @GraphBooleanProperty(value = "expanded_access_type_treatment", truthValue = "t")
    public String expandedAccessTypeTreatment;
    @JsonProperty("has_dmc")
    @GraphBooleanProperty(value = "has_dmc", truthValue = "t")
    public String hasDmc;
    @JsonProperty("is_fda_regulated_drug")
    @GraphBooleanProperty(value = "is_fda_regulated_drug", truthValue = "t")
    public String isFdaRegulatedDrug;
    @JsonProperty("is_fda_regulated_device")
    @GraphBooleanProperty(value = "is_fda_regulated_device", truthValue = "t")
    public String isFdaRegulatedDevice;
    @JsonProperty("is_unapproved_device")
    @GraphBooleanProperty(value = "is_unapproved_device", truthValue = "t")
    public String isUnapprovedDevice;
    @JsonProperty("is_ppsd")
    @GraphBooleanProperty(value = "is_ppsd", truthValue = "t")
    public String isPpsd;
    @JsonProperty("is_us_export")
    @GraphBooleanProperty(value = "is_us_export", truthValue = "t")
    public String isUsExport;
    @JsonProperty("biospec_retention")
    @GraphProperty("biospec_retention")
    public String biospecRetention;
    @JsonProperty("biospec_description")
    @GraphProperty("biospec_description")
    public String biospecDescription;
    @JsonProperty("ipd_time_frame")
    @GraphProperty("ipd_time_frame")
    public String ipdTimeFrame;
    @JsonProperty("ipd_access_criteria")
    @GraphProperty("ipd_access_criteria")
    public String ipdAccessCriteria;
    @JsonProperty("ipd_url")
    @GraphProperty("ipd_url")
    public String ipdUrl;
    @JsonProperty("plan_to_share_ipd")
    @GraphProperty("plan_to_share_ipd")
    public String planToShareIpd;
    @JsonProperty("plan_to_share_ipd_description")
    @GraphProperty("plan_to_share_ipd_description")
    public String planToShareIpdDescription;
    @JsonProperty("created_at")
    @GraphProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    @GraphProperty("updated_at")
    public String updatedAt;
}
