package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

public class ClinicalStudy {
    @JsonProperty(value = "required_header", required = true)
    public RequiredHeaderStruct requiredHeader;
    @JsonProperty(value = "id_info", required = true)
    public IdInfoStruct idInfo;
    @JsonProperty(value = "brief_title", required = true)
    public String briefTitle;
    public String acronym;
    @JsonProperty(value = "official_title")
    public String officialTitle;
    @JsonProperty(required = true)
    public SponsorsStruct sponsors;
    @JsonProperty(required = true)
    public String source;
    @JsonProperty(value = "oversight_info")
    public OversightInfoStruct oversightInfo;
    @JsonProperty(value = "brief_summary")
    public TextblockStruct briefSummary;
    @JsonProperty(value = "detailed_description")
    public TextblockStruct detailedDescription;
    @JsonProperty(value = "overall_status", required = true)
    public String overallStatus;
    @JsonProperty(value = "last_known_status")
    public String lastKnownStatus;
    @JsonProperty(value = "why_stopped")
    public String whyStopped;
    @JsonProperty(value = "start_date")
    public VariableDateStruct startDate;
    @JsonProperty(value = "completion_date")
    public VariableDateStruct completionDate;
    @JsonProperty(value = "primary_completion_date")
    public VariableDateStruct primaryCompletionDate;
    public PhaseEnum phase;
    @JsonProperty(value = "study_type", required = true)
    public StudyTypeEnum studyType;
    @JsonProperty(value = "has_expanded_access")
    public YesNoEnum hasExpandedAccess;
    @JsonProperty(value = "expanded_access_info")
    public ExpandedAccessInfoStruct expandedAccessInfo;
    @JsonProperty(value = "study_design_info")
    public StudyDesignInfoStruct studyDesignInfo;
    @JsonProperty(value = "target_duration")
    public String targetDuration;
    @JsonProperty(value = "primary_outcome")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ProtocolOutcomeStruct> primaryOutcome;
    @JsonProperty(value = "secondary_outcome")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ProtocolOutcomeStruct> secondaryOutcome;
    @JsonProperty(value = "other_outcome")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ProtocolOutcomeStruct> otherOutcome;
    @JsonProperty(value = "number_of_arms")
    public Integer numberOfArms;
    @JsonProperty(value = "number_of_groups")
    public Integer numberOfGroups;
    public EnrollmentStruct enrollment;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> condition;
    @JsonProperty(value = "arm_group")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ArmGroupStruct> armGroup;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<InterventionStruct> intervention;
    @JsonProperty(value = "biospec_retention")
    public BiospecRetentionEnum biospecRetention;
    @JsonProperty(value = "biospec_descr")
    public TextblockStruct biospecDescr;
    public EligibilityStruct eligibility;
    @JsonProperty(value = "overall_official")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<InvestigatorStruct> overallOfficial;
    @JsonProperty(value = "overall_contact")
    public ContactStruct overallContact;
    @JsonProperty(value = "overall_contact_backup")
    public ContactStruct overallContactBackup;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<LocationStruct> location;
    @JsonProperty(value = "location_countries")
    public CountriesStruct locationCountries;
    @JsonProperty(value = "removed_countries")
    public CountriesStruct removedCountries;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<LinkStruct> link;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ReferenceStruct> reference;
    @JsonProperty(value = "results_reference")
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<ReferenceStruct> resultsReference;
    @JsonProperty(value = "verification_date")
    public String verificationDate;
    @JsonProperty(value = "study_first_submitted")
    public String studyFirstSubmitted;
    @JsonProperty(value = "study_first_submitted_qc")
    public String studyFirstSubmittedQc;
    @JsonProperty(value = "study_first_posted")
    public VariableDateStruct studyFirstPosted;
    @JsonProperty(value = "results_first_submitted")
    public String resultsFirstSubmitted;
    @JsonProperty(value = "results_first_submitted_qc")
    public String resultsFirstSubmittedQc;
    @JsonProperty(value = "results_first_posted")
    public VariableDateStruct resultsFirstPosted;
    @JsonProperty(value = "disposition_first_submitted")
    public String dispositionFirstSubmitted;
    @JsonProperty(value = "disposition_first_submitted_qc")
    public String dispositionFirstSubmittedQc;
    @JsonProperty(value = "disposition_first_posted")
    public VariableDateStruct dispositionFirstPosted;
    @JsonProperty(value = "last_update_submitted")
    public String lastUpdateSubmitted;
    @JsonProperty(value = "last_update_submitted_qc")
    public String lastUpdateSubmittedQc;
    @JsonProperty(value = "last_update_posted")
    public VariableDateStruct lastUpdatePosted;
    @JsonProperty(value = "responsible_party")
    public ResponsiblePartyStruct responsibleParty;
    @JacksonXmlElementWrapper(useWrapping = false)
    public ArrayList<String> keyword;
    @JsonProperty(value = "condition_browse")
    public BrowseStruct conditionBrowse;
    @JsonProperty(value = "intervention_browse")
    public BrowseStruct interventionBrowse;
    @JsonProperty(value = "patient_data")
    public PatientDataStruct patientData;
    @JsonProperty(value = "study_docs")
    public StudyDocsStruct studyDocs;
    @JsonProperty(value = "provided_document_section")
    public ProvidedDocumentSectionStruct providedDocumentSection;
    @JsonProperty(value = "pending_results")
    public PendingResultsStruct pendingResults;
    @JsonProperty(value = "clinical_results")
    public ClinicalResultsStruct clinicalResults;
    @JacksonXmlProperty(isAttribute = true)
    public String rank;
}
