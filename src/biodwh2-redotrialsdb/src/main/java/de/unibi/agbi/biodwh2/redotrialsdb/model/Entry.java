package de.unibi.agbi.biodwh2.redotrialsdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "NCT Number", "Title", "Acronym", "Status", "Conditions", "Interventions", "Outcome Measures", "Sponsors",
        "Gender", "Age", "Enrollment", "Funders", "Study Type", "Study Designs", "Other IDs", "Start Date",
        "Primary Completion Date", "Completion Date", "Last Verified", "First Submitted", "First Posted",
        "Results First Submitted", "Results First Posted", "Last Update Submitted", "Last Update Posted", "URL",
        "Setting", "Stage", "Sponsor_Type", "Controlled", "Multi-Arm", "Pediatric", "Country_PI", "Cancer_Group",
        "Cancer_Type", "Drug_INN", "Primary-EP", "Phase", "DrugBank", "Removed"
})
public final class Entry {
    @JsonProperty("NCT Number")
    public String nctNumber;
    @JsonProperty("Title")
    public String title;
    @JsonProperty("Acronym")
    public String acronym;
    @JsonProperty("Status")
    public String status;
    @JsonProperty("Conditions")
    public String conditions;
    @JsonProperty("Interventions")
    public String interventions;
    @JsonProperty("Outcome Measures")
    public String outcomeMeasures;
    @JsonProperty("Sponsors")
    public String sponsors;
    @JsonProperty("Gender")
    public String gender;
    @JsonProperty("Age")
    public String age;
    @JsonProperty("Enrollment")
    public String enrollment;
    @JsonProperty("Funders")
    public String funders;
    @JsonProperty("Study Type")
    public String studyType;
    @JsonProperty("Study Designs")
    public String studyDesigns;
    @JsonProperty("Other IDs")
    public String otherIds;
    @JsonProperty("Start Date")
    public String startDate;
    @JsonProperty("Primary Completion Date")
    public String primaryCompletionDate;
    @JsonProperty("Completion Date")
    public String completionDate;
    @JsonProperty("Last Verified")
    public String lastVerified;
    @JsonProperty("First Submitted")
    public String firstSubmitted;
    @JsonProperty("First Posted")
    public String firstPosted;
    @JsonProperty("Results First Submitted")
    public String resultsFirstSubmitted;
    @JsonProperty("Results First Posted")
    public String resultsFirstPosted;
    @JsonProperty("Last Update Submitted")
    public String lastUpdateSubmitted;
    @JsonProperty("Last Update Posted")
    public String lastUpdatePosted;
    @JsonProperty("URL")
    public String url;
    @JsonProperty("Setting")
    public String setting;
    @JsonProperty("Stage")
    public String stage;
    @JsonProperty("Sponsor_Type")
    public String sponsorType;
    @JsonProperty("Controlled")
    public String controlled;
    @JsonProperty("Multi-Arm")
    public String multiArm;
    @JsonProperty("Pediatric")
    public String pediatric;
    @JsonProperty("Country_PI")
    public String countryPI;
    @JsonProperty("Cancer_Group")
    public String cancerGroup;
    @JsonProperty("Cancer_Type")
    public String cancerType;
    @JsonProperty("Drug_INN")
    public String drugINN;
    @JsonProperty("Primary-EP")
    public String primaryEP;
    @JsonProperty("Phase")
    public String phase;
    @JsonProperty("DrugBank")
    public String drugBank;
    @JsonProperty("Removed")
    public String removed;
}