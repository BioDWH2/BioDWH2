package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class ResultsOutcomeStruct {
    @JsonProperty(required = true)
    public OutcomeTypeEnum type;
    @JsonProperty(required = true)
    public String title;
    public String description;
    @JsonProperty(value = "time_frame")
    public String timeFrame;
    @JsonProperty(value = "safety_issue")
    public YesNoEnum safetyIssue;
    @JsonProperty(value = "posting_date")
    public String postingDate;
    public String population;
    @JsonProperty(value = "group_list")
    public ResultsOutcomeStruct.GroupList groupList;
    public MeasureStruct measure;
    @JsonProperty(value = "analysis_list")
    public ResultsOutcomeStruct.AnalysisList analysisList;

    public static class AnalysisList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<AnalysisStruct> analysis;
    }

    public static class GroupList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<GroupStruct> group;
    }
}
