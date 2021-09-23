package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "outcome_type", "title", "description", "time_frame", "population", "anticipated_posting_date",
        "anticipated_posting_month_year", "units", "units_analyzed", "dispersion_type", "param_type"
})
public class Outcome {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("outcome_type")
    public String outcomeType;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("time_frame")
    public String timeFrame;
    @JsonProperty("population")
    public String population;
    @JsonProperty("anticipated_posting_date")
    public String anticipatedPostingDate;
    @JsonProperty("anticipated_posting_month_year")
    public String anticipatedPostingMonthYear;
    @JsonProperty("units")
    public String units;
    @JsonProperty("units_analyzed")
    public String unitsAnalyzed;
    @JsonProperty("dispersion_type")
    public String dispersionType;
    @JsonProperty("param_type")
    public String paramType;
}
