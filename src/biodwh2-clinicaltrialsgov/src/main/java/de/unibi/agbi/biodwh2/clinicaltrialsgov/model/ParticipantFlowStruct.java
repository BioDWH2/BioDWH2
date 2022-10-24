package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class ParticipantFlowStruct {
    @JsonProperty(value = "recruitment_details")
    public String recruitmentDetails;
    @JsonProperty(value = "pre_assignment_details")
    public String preAssignmentDetails;
    @JsonProperty(value = "group_list", required = true)
    public ParticipantFlowStruct.GroupList groupList;
    @JsonProperty(value = "period_list", required = true)
    public ParticipantFlowStruct.PeriodList periodList;

    public static class GroupList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<GroupStruct> group;
    }

    public static class PeriodList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<PeriodStruct> period;
    }
}
