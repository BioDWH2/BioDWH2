package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;

@JsonPropertyOrder({
        "id", "nct_id", "number_of_facilities", "number_of_nsae_subjects", "number_of_sae_subjects",
        "registered_in_calendar_year", "nlm_download_date", "actual_duration", "were_results_reported",
        "months_to_report_results", "has_us_facility", "has_single_facility", "minimum_age_num", "maximum_age_num",
        "minimum_age_unit", "maximum_age_unit", "number_of_primary_outcomes_to_measure",
        "number_of_secondary_outcomes_to_measure", "number_of_other_outcomes_to_measure"
})
public class CalculatedValue {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("number_of_facilities")
    public Integer numberOfFacilities;
    @JsonProperty("number_of_nsae_subjects")
    public Integer numberOfNsaeSubjects;
    @JsonProperty("number_of_sae_subjects")
    public Integer numberOfSaeSubjects;
    @JsonProperty("registered_in_calendar_year")
    public Integer registeredInCalendarYear;
    @JsonProperty("nlm_download_date")
    public String nlmDownloadDate;
    @JsonProperty("actual_duration")
    public String actualDuration;
    @GraphBooleanProperty(value = "were_results_reported", truthValue = "t")
    public String wereResultsReported;
    @JsonProperty("months_to_report_results")
    public String monthsToReportResults;
    @GraphBooleanProperty(value = "has_us_facility", truthValue = "t")
    public String hasUsFacility;
    @GraphBooleanProperty(value = "has_single_facility", truthValue = "t")
    public String hasSingleFacility;
    @JsonProperty("minimum_age_num")
    public String minimumAgeNum;
    @JsonProperty("maximum_age_num")
    public String maximumAgeNum;
    @JsonProperty("minimum_age_unit")
    public String minimumAgeUnit;
    @JsonProperty("maximum_age_unit")
    public String maximumAgeUnit;
    @JsonProperty("number_of_primary_outcomes_to_measure")
    public Integer numberOfPrimaryOutcomesToMeasure;
    @JsonProperty("number_of_secondary_outcomes_to_measure")
    public Integer numberOfSecondaryOutcomesToMeasure;
    @JsonProperty("number_of_other_outcomes_to_measure")
    public Integer numberOfOtherOutcomesToMeasure;
}
