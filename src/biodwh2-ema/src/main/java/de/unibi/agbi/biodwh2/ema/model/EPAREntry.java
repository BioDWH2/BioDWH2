package de.unibi.agbi.biodwh2.ema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@JsonPropertyOrder({
        "Category", "Medicine name", "Therapeutic area", "International non-proprietary name (INN) / common name",
        "Active substance", "Product number", "Patient safety", "Authorisation status", "ATC code",
        "Additional monitoring", "Generic", "Biosimilar", "Conditional approval", "Exceptional circumstances",
        "Accelerated assessment", "Orphan medicine", "Marketing authorisation date",
        "Date of refusal of marketing authorisation", "Marketing authorisation holder/company name",
        "Human pharmacotherapeutic group", "Vet pharmacotherapeutic group", "Date of opinion", "Decision date",
        "Revision number", "Condition / indication", "Species", "ATCvet code", "First published", "Revision date", "URL"
})
@GraphNodeLabel("EPAREntry")
public class EPAREntry {
    @JsonProperty("Category")
    @GraphProperty("category")
    public String category;
    @JsonProperty("Medicine name")
    @GraphProperty("medicine_name")
    public String medicineName;
    @JsonProperty("Therapeutic area")
    @GraphProperty("therapeutic_area")
    public String therapeuticArea;
    @JsonProperty("International non-proprietary name (INN) / common name")
    @GraphProperty("inn_or_common_name")
    public String innOrCommonName;
    @JsonProperty("Active substance")
    @GraphProperty("active_substance")
    public String activeSubstance;
    @JsonProperty("Product number")
    @GraphProperty("product_number")
    public String productNumber;
    @JsonProperty("Patient safety")
    @GraphBooleanProperty("patient_safety")
    public Boolean patientSafety;
    @JsonProperty("Authorisation status")
    @GraphProperty("authorisation_status")
    public String authorisationStatus;
    @JsonProperty("ATC code")
    @GraphProperty("atc_code")
    public String atcCode;
    @JsonProperty("Additional monitoring")
    @GraphBooleanProperty("additional_monitoring")
    public Boolean additionalMonitoring;
    @JsonProperty("Generic")
    @GraphBooleanProperty("generic")
    public Boolean generic;
    @JsonProperty("Biosimilar")
    @GraphBooleanProperty("biosimilar")
    public Boolean biosimilar;
    @JsonProperty("Conditional approval")
    @GraphBooleanProperty("conditional_approval")
    public Boolean conditionalApproval;
    @JsonProperty("Exceptional circumstances")
    @GraphBooleanProperty("exceptional_circumstances")
    public Boolean exceptionalCircumstances;
    @JsonProperty("Accelerated assessment")
    @GraphBooleanProperty("accelerated_assessment")
    public Boolean acceleratedAssessment;
    @JsonProperty("Orphan medicine")
    @GraphBooleanProperty("orphan_medicine")
    public Boolean orphanMedicine;
    @JsonProperty("Marketing authorisation date")
    @GraphProperty("marketing_authorisation_date")
    public String marketingAuthorisationDate;
    @JsonProperty("Date of refusal of marketing authorisation")
    @GraphProperty("date_of_refusal_of_marketing_authorisation")
    public String dateOfRefusalOfMarketingAuthorisation;
    @JsonProperty("Marketing authorisation holder/company name")
    @GraphProperty("marketing_authorisation_holder_or_company_name")
    public String marketingAuthorisationHolderOrCompanyName;
    @JsonProperty("Human pharmacotherapeutic group")
    @GraphProperty("human_pharmacotherapeutic_group")
    public String humanPharmacotherapeuticGroup;
    @JsonProperty("Vet pharmacotherapeutic group")
    @GraphProperty("vet_pharmacotherapeutic_group")
    public String vetPharmacotherapeuticGroup;
    @JsonProperty("Date of opinion")
    @GraphProperty("date_of_opinion")
    public String dateOfOpinion;
    @JsonProperty("Decision date")
    @GraphProperty("decision_date")
    public String decisionDate;
    @JsonProperty("Revision number")
    @GraphProperty("revision_number")
    public Integer revisionNumber;
    @JsonProperty("Condition / indication")
    @GraphProperty("condition_or_indication")
    public String conditionOrIndication;
    @JsonProperty("Species")
    @GraphProperty("species")
    public String species;
    @JsonProperty("ATCvet code")
    @GraphProperty("atc_vet_code")
    public String atcVetCode;
    @JsonProperty("First published")
    @GraphProperty("first_published")
    public String firstPublished;
    @JsonProperty("Revision date")
    @GraphProperty("revision_date")
    public String revisionDate;
    @JsonProperty("URL")
    @GraphProperty("url")
    public String url;
}
